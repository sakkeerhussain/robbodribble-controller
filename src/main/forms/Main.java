package main.forms;

import main.sensor.Sensor;
import main.sensor.SensorsManager;

import javax.swing.*;

public class Main {
    private JTabbedPane tabbedPane1;
    private JPanel pRoot;
    private JTabbedPane tpCalibration;
    private JPanel pBalls;

    private Main() {
        for (Sensor sensor: SensorsManager.Companion.getSENSORS_LIST()) {
            addCalibrationForm(sensor.getIp());
        }

        setupBallsForm();
    }

    private void setupBallsForm() {
        pBalls.add(new BallsForm().pRoot);
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
