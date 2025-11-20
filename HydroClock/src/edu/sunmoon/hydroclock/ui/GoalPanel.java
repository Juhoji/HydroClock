package edu.sunmoon.hydroclock.ui;

import javax.swing.*;
import java.awt.*;
import edu.sunmoon.hydroclock.core.Storage;

public class GoalPanel extends JPanel {
    public GoalPanel(Storage storage, Runnable onChange) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));
        add(new JLabel("하루 목표(mL):"));
        JTextField tf = new JTextField(String.valueOf(storage.getGoalMl()), 8);
        JButton save = new JButton("저장");
        save.addActionListener(e -> {
            try {
                int v = Integer.parseInt(tf.getText().trim());
                storage.setGoalMl(v);
                if (onChange != null) onChange.run();
                JOptionPane.showMessageDialog(this, "저장되었습니다.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "숫자를 입력하세요.");
            }
        });
        add(tf); add(save);
    }
}
