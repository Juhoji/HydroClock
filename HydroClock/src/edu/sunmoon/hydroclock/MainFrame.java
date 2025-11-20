package edu.sunmoon.hydroclock;

import javax.swing.*;
import java.awt.*;
import edu.sunmoon.hydroclock.ui.*;
import edu.sunmoon.hydroclock.core.Storage;

public class MainFrame extends JFrame {

    private final Storage storage = new Storage();  // ⬅️ 추가

    public MainFrame() {
        super("HydroClock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 640);
        setLocationRelativeTo(null);

        // 각 패널에 storage 전달 + 홈 새로고침 콜백 연결
        HomePanel home       = new HomePanel(storage);
        RecordPanel record   = new RecordPanel(storage, home::refresh);
        GoalPanel goal       = new GoalPanel(storage, home::refresh);
        StatsPanel stats     = new StatsPanel(storage);
        SettingsPanel setts  = new SettingsPanel(storage /*추후 알림 서비스 연동*/);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("홈",   home);
        tabs.addTab("기록", record);
        tabs.addTab("목표", goal);
        tabs.addTab("통계", stats);
        tabs.addTab("설정", setts);

        setContentPane(tabs);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(10,10,10,10));
    }
}
