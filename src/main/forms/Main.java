package main.forms;

import javax.swing.*;

public class Main {
    private JTabbedPane tabbedPane1;
    private JPanel pRoot;
    private JTabbedPane tpCalibration;

    private Main() {
        addCalibrationForm("10.7.170.6");
        //addCalibrationForm("10.7.170.7");
    }

    private void addCalibrationForm(String ip) {
        tpCalibration.add(new CalibrationForm(ip).rootPanel);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new Main().pRoot);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
