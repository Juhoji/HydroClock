package edu.sunmoon.hydroclock.core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Properties;

public class Storage {
    private final Path dir        = Paths.get(System.getProperty("user.home"), ".hydroclock");
    private final Path propsFile  = dir.resolve("settings.properties");
    private final Path intakeFile = dir.resolve("intake.csv");
    private final Path notifyLog  = dir.resolve("notify_log.csv");
    private final Properties props = new Properties();

    public Storage() {
        try {
            if (!Files.exists(dir)) Files.createDirectories(dir);
            if (Files.exists(propsFile)) {
                try (var in = Files.newBufferedReader(propsFile, StandardCharsets.UTF_8)) {
                    props.load(in);
                }
            } else {
                props.setProperty("goal.ml", "2000");
                props.setProperty("interval.min", "60");
                saveProps();
            }
            if (!Files.exists(intakeFile)) {
                Files.createFile(intakeFile);
                try (var pw = new PrintWriter(Files.newBufferedWriter(intakeFile, StandardCharsets.UTF_8))) {
                    pw.println("date,type,amount,unit,k,effective_ml");
                }
            }
            if (!Files.exists(notifyLog)) {
                Files.createFile(notifyLog);
                try (var pw = new PrintWriter(Files.newBufferedWriter(notifyLog, StandardCharsets.UTF_8))) {
                    pw.println("timestamp,title,message");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Storage 초기화 실패: " + e.getMessage(), e);
        }
    }

    // ----- 설정 -----
    public int getGoalMl() { return Integer.parseInt(props.getProperty("goal.ml", "2000")); }
    public void setGoalMl(int ml) {
        props.setProperty("goal.ml", String.valueOf(Math.max(500, ml)));
        saveProps();
    }
    public int getIntervalMin() { return Integer.parseInt(props.getProperty("interval.min", "60")); }
    public void setIntervalMin(int min) {
        props.setProperty("interval.min", String.valueOf(Math.max(5, min)));
        saveProps();
    }
    private void saveProps() {
        try (var out = Files.newBufferedWriter(propsFile, StandardCharsets.UTF_8)) {
            props.store(out, "HydroClock Settings");
        } catch (IOException ignored) {}
    }

    // ----- 기록 -----
    public void addIntake(double effectiveMl, String type, double amount, String unit, double k) {
        String d = LocalDate.now().toString();
        try (var bw = Files.newBufferedWriter(intakeFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            String row = String.join(",", d, safe(type), String.valueOf(amount), safe(unit),
                    String.valueOf(k), String.valueOf(Math.round(effectiveMl)));
            bw.write(row);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("기록 저장 실패: " + e.getMessage(), e);
        }
    }
    private String safe(String s) { return (s == null ? "" : s.replace(",", " ")); }

    // ----- 알림 로그 -----
    public void appendNotifyLog(String title, String message) {
        String ts = java.time.LocalDateTime.now().toString();
        try (var bw = Files.newBufferedWriter(notifyLog, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            String row = String.join(",", ts, safe(title), safe(message));
            bw.write(row);
            bw.newLine();
        } catch (IOException ignored) {}
    }

    // ----- 집계 -----
    public int getTodayTotal() {
        String today = LocalDate.now().toString();
        int sum = 0;
        try (var br = Files.newBufferedReader(intakeFile, StandardCharsets.UTF_8)) {
            String line = br.readLine(); // header skip
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                if (p.length >= 6 && p[0].equals(today))
                    sum += safeParse(p[5]);
            }
        } catch (IOException ignored) {}
        return sum;
    }

    public LinkedHashMap<String, Integer> getLast7Days() {
        var map = new LinkedHashMap<String, Integer>();
        for (int i = 6; i >= 0; i--)
            map.put(LocalDate.now().minusDays(i).toString(), 0);
        try (var br = Files.newBufferedReader(intakeFile, StandardCharsets.UTF_8)) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                if (p.length >= 6 && map.containsKey(p[0])) {
                    map.put(p[0], map.get(p[0]) + safeParse(p[5]));
                }
            }
        } catch (IOException ignored) {}
        return map;
    }

    private int safeParse(String s) {
        if (s == null) return 0;
        try { return (int)Math.round(Double.parseDouble(s.trim())); }
        catch (NumberFormatException e) { return 0; }
    }

    // ----- 데이터 관리 -----
    public void resetIntakeFile() throws IOException {
        // 헤더만 남기기
        Files.writeString(intakeFile, "date,type,amount,unit,k,effective_ml\n", StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void exportIntakeFile(Path target) throws IOException {
        Files.copy(intakeFile, target, StandardCopyOption.REPLACE_EXISTING);
    }
}


