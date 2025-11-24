package edu.sunmoon.hydroclock.core;

import javax.swing.*;
import java.awt.*;

public class PopupNotifier implements Notifier {
    private boolean beep = true;
    private final Component parent;
    private final Storage storage; // for logging

    public PopupNotifier(Component parent) {
        this.parent = parent;
        Storage s = null;
        // try to access Storage if parent is MainFrame (not required; safe to keep null)
        this.storage = null;
    }

    @Override
    public void notify(String title, String message) {
        if (beep) Toolkit.getDefaultToolkit().beep();
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE)
        );
    }

    @Override
    public void setBeep(boolean enabled) { this.beep = enabled; }
}

