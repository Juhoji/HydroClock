package edu.sunmoon.hydroclock;

import javax.swing.SwingUtilities;

public class HydroClockApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}