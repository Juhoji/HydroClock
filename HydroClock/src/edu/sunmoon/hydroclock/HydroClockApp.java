package edu.sunmoon.hydroclock;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class HydroClockApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MainFrame().setVisible(true);
            } catch (Throwable t) {
                t.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        t.toString(), "에러 발생", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

