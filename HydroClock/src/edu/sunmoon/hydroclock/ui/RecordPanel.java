package edu.sunmoon.hydroclock.ui;

import javax.swing.*;
import java.awt.*;
import edu.sunmoon.hydroclock.core.Storage;

public class RecordPanel extends JPanel {
    public RecordPanel(Storage storage, Runnable onChange) {
        setLayout(new BorderLayout(8,8));
        add(new JLabel("빠른 기록(프리셋)"), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2,3,8,8));
        int[] preset = {100,150,200,250,300,500};
        for (int ml : preset) {
            JButton b = new JButton(ml + " mL");
            b.addActionListener(e -> {
                storage.addIntake(ml, "water", ml, "ml", 1.0);
                if (onChange != null) onChange.run();
            });
            grid.add(b);
        }
        add(grid, BorderLayout.CENTER);

        // (선택) 간접 섭취 예시 버튼
        JButton coffee = new JButton("커피 250 mL (k=0.95)");
        coffee.addActionListener(e -> {
            storage.addIntake(250*0.95, "coffee", 250, "ml", 0.95);
            if (onChange != null) onChange.run();
        });
        add(coffee, BorderLayout.SOUTH);
    }
}
