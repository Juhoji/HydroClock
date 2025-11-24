package edu.sunmoon.hydroclock.ui;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import edu.sunmoon.hydroclock.core.Storage;
import edu.sunmoon.hydroclock.core.ReminderService;

public class SettingsPanel extends JPanel {
    public SettingsPanel(Storage storage, ReminderService reminder){
        setLayout(new FlowLayout(FlowLayout.LEFT,12,12));
        add(new JLabel("알림 간격(분):"));
        JTextField tf = new JTextField(String.valueOf(storage.getIntervalMin()),5);
        JCheckBox beep = new JCheckBox("비프음", true);
        JButton apply = new JButton("적용(저장 & 재시작)");

        apply.addActionListener(e->{
            try{
                int v = Integer.parseInt(tf.getText().trim());
                storage.setIntervalMin(v);
                reminder.restart(v);
                JOptionPane.showMessageDialog(this,"저장되었습니다. 알림 주기를 재시작합니다.");
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(this,"숫자를 입력하세요.");
            }
        });

        beep.addActionListener(e->reminder.setBeep(beep.isSelected()));

        // 데이터 관리 버튼들
        JButton reset = new JButton("섭취기록 초기화");
        reset.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this, "모든 섭취 기록을 초기화하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                try {
                    storage.resetIntakeFile();
                    JOptionPane.showMessageDialog(this, "섭취 기록이 초기화되었습니다.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "초기화 실패: " + ex.getMessage());
                }
            }
        });

        JButton export = new JButton("CSV 내보내기");
        export.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("CSV 내보내기");
            int res = fc.showSaveDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                Path p = fc.getSelectedFile().toPath();
                try {
                    storage.exportIntakeFile(p);
                    JOptionPane.showMessageDialog(this, "내보내기 완료: " + p.toString());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "내보내기 실패: " + ex.getMessage());
                }
            }
        });

        add(tf); add(apply); add(beep);
        add(Box.createHorizontalStrut(10));
        add(reset); add(export);
    }
}

