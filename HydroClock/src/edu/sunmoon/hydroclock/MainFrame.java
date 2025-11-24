package edu.sunmoon.hydroclock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import edu.sunmoon.hydroclock.core.*;
import edu.sunmoon.hydroclock.ui.*;

public class MainFrame extends JFrame {

    private final Storage storage = new Storage();
    private final ReminderService reminder =
            new ReminderService(new PopupNotifier(this));

    public MainFrame() {
        super("HydroClock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 600); // 통계 그래프 가독성을 위해 가로 넉넉히
        setLocationRelativeTo(null);

        // 각 화면
        HomePanel home      = new HomePanel(storage);
        RecordPanel record  = new RecordPanel(storage, home::refresh);
        GoalPanel goal      = new GoalPanel(storage, home::refresh);
        StatsPanel stats    = new StatsPanel(storage);
        SettingsPanel sets  = new SettingsPanel(storage, reminder);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("홈", home);
        tabs.addTab("기록", record);
        tabs.addTab("목표", goal);
        tabs.addTab("통계", stats);
        tabs.addTab("설정", sets);
        setContentPane(tabs);

        // 앱 시작 시 알림 시작
        reminder.start(storage.getIntervalMin());

        // 창 닫을 때 스레드 정리
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                reminder.shutdown();
            }
        });

        UIManager.put("TabbedPane.contentBorderInsets", new Insets(10,10,10,10));
    }
}

