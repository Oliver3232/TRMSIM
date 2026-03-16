package es.ants.felixgm.trmsim_wsn.gui;

import java.awt.Toolkit;

final class MainWindowFrameSetup {
    private MainWindowFrameSetup() {
    }

    static void configure(TRMSim_WSN window) {
        window.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        window.setTitle("TRMSim-WSN " + TRMSim_WSN.CURRENT_VERSION + " Trust & Reputation Models Simulator for Wireless Sensor Networks");
        window.setBounds(new java.awt.Rectangle(100, 100, 0, 0));
        window.setIconImage(Toolkit.getDefaultToolkit().getImage("resources/images/TRMSim-WSN-icon.gif"));
        window.setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());
        window.setPreferredSize(new java.awt.Dimension(100, 100));
        window.getContentPane().setLayout(new javax.swing.BoxLayout(window.getContentPane(), javax.swing.BoxLayout.Y_AXIS));
        window.tabbedPane.setPreferredSize(window.getSize());
    }
}
