package edu.sunmoon.hydroclock.ui;

import javax.swing.*;
import java.awt.*;
import edu.sunmoon.hydroclock.core.Storage;

public class SettingsPanel extends JPanel {
    public SettingsPanel(Storage storage) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));
        add(new JLabel("알림 간격(분):"));
        JTextField tf = new JTextField(String.valueOf(storage.getIntervalMin()), 5);
        JButton apply = new JButton("적용(저장)");
        apply.addActionListener(e -> {
            try {
                storage.setIntervalMin(Integer.parseInt(tf.getText().trim()));
                JOptionPane.showMessageDialog(this, "알림 간격이 저장되었습니다.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "숫자를 입력하세요.");
            }
        });
        add(tf); add(apply);
    }
}
