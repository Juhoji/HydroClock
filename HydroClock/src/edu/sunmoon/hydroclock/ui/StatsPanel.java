package edu.sunmoon.hydroclock.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import edu.sunmoon.hydroclock.core.Storage;

public class StatsPanel extends JPanel {
    private final Storage storage;
    private final JTextArea ta = new JTextArea();
    private final ChartPanel chart = new ChartPanel();

    public StatsPanel(Storage storage) {
        this.storage = storage;
        setLayout(new BorderLayout(10,10));

        ta.setEditable(false);
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane left = new JScrollPane(ta);
        left.setPreferredSize(new Dimension(260, 400));

        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.add(chart, BorderLayout.CENTER);

        JPanel main = new JPanel(new BorderLayout(6,6));
        main.add(left, BorderLayout.WEST);
        main.add(rightContainer, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);

        JButton refresh = new JButton("새로고침");
        refresh.addActionListener(e -> load());
        add(refresh, BorderLayout.SOUTH);

        load();
    }

    private void load() {
        Map<String,Integer> m = storage.getLast7Days();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s %10s%n", "날짜", "섭취량(ml)"));
        sb.append("------------------------------\n");
        m.forEach((date, sum) -> sb.append(String.format("%-12s %10d%n", date, sum)));
        ta.setText(sb.toString());

        chart.setData(m.values().stream().mapToInt(v -> v).toArray(),
                      m.keySet().toArray(new String[0]));
    }

    /** 간단 바차트를 그리는 내부 패널 */
    static class ChartPanel extends JPanel {
        private int[] values = new int[0];
        private String[] labels = new String[0];

        void setData(int[] v, String[] lab) {
            this.values = v;
            this.labels = lab;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (values.length == 0) return;

            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            int pad = 40, gap = 10;
            int chartW = Math.max(100, w - pad*2);
            int chartH = Math.max(100, h - pad*2);
            int n = values.length;
            int barW = Math.max(15, (chartW - gap*(n-1)) / n);
            int max = 1;
            for (int v : values) max = Math.max(max, v);

            // axis
            g2.drawLine(pad, h - pad, w - pad, h - pad);
            g2.drawLine(pad, pad, pad, h - pad);

            // bars
            for (int i = 0; i < n; i++) {
                int x = pad + i * (barW + gap);
                int barH = (int) Math.round(values[i] * (chartH * 1.0 / max));
                int y = h - pad - barH;

                g2.setColor(Color.GREEN.darker());
                g2.fillRect(x, y, barW, barH);
                g2.setColor(Color.BLACK);
                g2.drawRect(x, y, barW, barH);

                // value label
                String v = String.valueOf(values[i]);
                g2.drawString(v, x + Math.max(2, barW/4), Math.max(y - 5, pad));

                // date label (MM-DD)
                String lab = labels[i].length() >= 5 ? labels[i].substring(5) : labels[i];
                g2.drawString(lab, x + 2, h - pad + 15);
            }
            g2.dispose();
        }
    }
}


