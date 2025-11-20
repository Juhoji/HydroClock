package edu.sunmoon.hydroclock.core;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class Storage {

    // 사용자 홈 아래 .hydroclock 폴더
    private final Path dir        = Paths.get(System.getProperty("user.home"), ".hydroclock");
    private final Path propsFile  = dir.resolve("settings.properties");
    private final Path intakeFile = dir.resolve("intake.csv");

    private final Properties props = new Properties();

    public Storage() {
        try {
            if (!Files.exists(dir)) Files.createDirectories(dir);

            // 설정 파일 로드(없으면 기본값 생성)
            if (Files.exists(propsFile)) {
                try (InputStream in = Files.newInputStream(propsFile)) {
                    props.load(in);
                }
            } else {
                props.setProperty("goal.ml", "2000");
                props.setProperty("interval.min", "60");
                saveProps();
            }

            // 섭취 로그 CSV 헤더 준비
            if (!Files.exists(intakeFile)) {
                try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(intakeFile))) {
                    pw.println("date,type,amount,unit,k,effective_ml");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("초기화 실패: " + e.getMessage(), e);
        }
    }

    // ---------- 설정 저장/읽기 ----------
    public int getGoalMl() {
        return Integer.parseInt(props.getProperty("goal.ml", "2000"));
    }
    public void setGoalMl(int ml) {
        int v = Math.max(500, ml);         // 최소 500mL 가드
        props.setProperty("goal.ml", String.valueOf(v));
        saveProps();
    }

    public int getIntervalMin() {
        return Integer.parseInt(props.getProperty("interval.min", "60"));
    }
    public void setIntervalMin(int min) {
        int v = Math.max(5, min);          // 최소 5분 가드
        props.setProperty("interval.min", String.valueOf(v));
        saveProps();
    }

    private void saveProps() {
        try (OutputStream out = Files.newOutputStream(propsFile)) {
            props.store(out, "HydroClock Settings");
        } catch (IOException ignored) {}
    }

    // ---------- 섭취 기록 ----------
    /** CSV에 1행 추가 (오늘 날짜 기준) */
    public void addIntake(double effectiveMl, String type, double amount, String unit, double k) {
        String d = LocalDate.now().toString();
        try (BufferedWriter bw = Files.newBufferedWriter(intakeFile, StandardOpenOption.APPEND)) {
            String row = String.join(",",
                    d,
                    safe(type),
                    String.valueOf(amount),
                    safe(unit),
                    String.valueOf(k),
                    String.valueOf(Math.round(effectiveMl)) // 표시용은 정수로 반올림 저장
            );
            bw.write(row);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("기록 저장 실패: " + e.getMessage(), e);
        }
    }

    private String safe(String s) { return s == null ? "" : s.replace(",", " "); }

    // ---------- 집계 ----------
    /** 오늘 유효 섭취량 합계(ml) */
    public int getTodayTotal() {
        String today = LocalDate.now().toString();
        int sum = 0;
        try (BufferedReader br = Files.newBufferedReader(intakeFile)) {
            String line = br.readLine(); // header skip
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 6 && p[0].equals(today)) {
                    int add = (int) Math.round(Double.parseDouble(p[5])); // ★ long→int
                    sum += add;
                }
            }
        } catch (IOException ignored) {}
        return sum;
    }

    /** 최근 7일(오늘 포함) 날짜별 합계(ml), 오래된 날짜부터 정렬 */
    public LinkedHashMap<String, Integer> getLast7Days() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) map.put(LocalDate.now().minusDays(i).toString(), 0);

        try (BufferedReader br = Files.newBufferedReader(intakeFile)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 6 && map.containsKey(p[0])) {
                    int prev = map.get(p[0]);
                    int add  = (int) Math.round(Double.parseDouble(p[5])); // ★ long→int
                    map.put(p[0], prev + add);
                }
            }
        } catch (IOException ignored) {}
        return map;
    }
}

