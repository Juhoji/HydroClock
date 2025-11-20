package edu.sunmoon.hydroclock.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import edu.sunmoon.hydroclock.core.Storage;

public class StatsPanel extends JPanel {
    private final Storage storage;
    private final JTextArea ta = new JTextArea();

    public StatsPanel(Storage storage) {
        this.storage = storage;
        setLayout(new BorderLayout(8,8));

        ta.setEditable(false);
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        add(new JScrollPane(ta), BorderLayout.CENTER);

        JButton refresh = new JButton("최근 7일 새로고침");
        refresh.addActionListener(e -> load());
        add(refresh, BorderLayout.SOUTH);

        load();
    }

    private void load() {
        Map<String,Integer> m = storage.getLast7Days();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s %8s%n","날짜","섭취량(ml)"));
        sb.append("-------------------------\n");
        m.forEach((date, sum) -> sb.append(String.format("%-12s %8d%n", date, sum)));
        ta.setText(sb.toString());
    }
}
