package edu.sunmoon.hydroclock.ui;

import javax.swing.*;
import java.awt.*;
import edu.sunmoon.hydroclock.core.Storage;

public class HomePanel extends JPanel {
    private final Storage storage;
    private final JLabel label = new JLabel("", SwingConstants.CENTER);
    private final JProgressBar bar = new JProgressBar(0,100);

    public HomePanel(Storage storage) {
        this.storage = storage;
        setLayout(new BorderLayout(10,10));

        JLabel title = new JLabel("오늘 섭취량 / 목표 진행률", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD,16f));
        bar.setStringPainted(true);

        JButton add = new JButton("200 mL 추가");
        add.addActionListener(e -> {
            storage.addIntake(200,"물",200,"ml",1.0);
            refresh();
        });

        add(title, BorderLayout.NORTH);
        add(label, BorderLayout.CENTER);
        add(bar, BorderLayout.SOUTH);
        add(add, BorderLayout.PAGE_END);

        refresh();
    }

    public void refresh() {
        int total = storage.getTodayTotal();
        int goal  = storage.getGoalMl();
        int pct = Math.min(100, (int)Math.round(100.0*total/Math.max(1,goal)));
        label.setText("오늘: "+total+" / "+goal+" mL");
        bar.setValue(pct);
        bar.setString(pct+"%");
    }
}


