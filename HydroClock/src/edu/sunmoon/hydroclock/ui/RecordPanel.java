package edu.sunmoon.hydroclock.ui;

import javax.swing.*;
import java.awt.*;
import edu.sunmoon.hydroclock.core.Storage;
import edu.sunmoon.hydroclock.core.HydrationMap;

public class RecordPanel extends JPanel {

    public RecordPanel(Storage storage, Runnable onChange) {
        setLayout(new BorderLayout(10,10));

        JLabel title = new JLabel("섭취 기록 (종류+용량 → 기록)", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        add(title, BorderLayout.NORTH);

        // 상단: 음료 종류 + 용량 입력
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        JComboBox<String> typeBox = new JComboBox<>(HydrationMap.all().keySet().toArray(new String[0]));
        JSpinner amount = new JSpinner(new SpinnerNumberModel(200, 50, 1000, 50)); // 50~1000mL, 50단위
        JLabel info = new JLabel("유효: 200 mL");

        // 유효량 표시 자동 갱신
        var updateInfo = (Runnable) () -> {
            String t = (String) typeBox.getSelectedItem();
            int ml = (int) amount.getValue();
            double k = HydrationMap.k(t);
            info.setText(String.format("유효: %.0f mL (k=%.2f)", ml * k, k));
        };
        typeBox.addActionListener(e -> updateInfo.run());
        amount.addChangeListener(e -> updateInfo.run());
        updateInfo.run();

        JButton addBtn = new JButton("기록");
        addBtn.addActionListener(e -> {
            String t = (String) typeBox.getSelectedItem();
            int ml = (int) amount.getValue();
            double k = HydrationMap.k(t);
            storage.addIntake(ml * k, t, ml, "ml", k);
            if (onChange != null) onChange.run();
            JOptionPane.showMessageDialog(this, "기록되었습니다.");
        });

        form.add(new JLabel("종류:"));
        form.add(typeBox);
        form.add(new JLabel("용량(mL):"));
        form.add(amount);
        form.add(addBtn);
        form.add(info);
        add(form, BorderLayout.CENTER);

        // 하단: 빠른 프리셋
        JPanel preset = new JPanel(new GridLayout(1,6,8,8));
        int[] ps = {100,150,200,250,300,500};
        for (int ml : ps) {
            JButton b = new JButton(ml + " mL(물)");
            b.addActionListener(e -> {
                storage.addIntake(ml, "물", ml, "ml", 1.0);
                if (onChange != null) onChange.run();
            });
            preset.add(b);
        }
        add(preset, BorderLayout.SOUTH);
    }
}

