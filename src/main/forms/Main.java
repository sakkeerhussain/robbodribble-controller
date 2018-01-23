package main.forms;

import main.sensor.Sensor;
import main.sensor.SensorsManager;

import javax.swing.*;

public class Main {
    private JTabbedPane tabbedPane1;
    private JPanel pRoot;
    private JTabbedPane tpCalibration;
    private JPanel pBalls;
    private JPanel pLog;

    private Main() {
        for (Sensor sensor: SensorsManager.Companion.get().getSensorsList()) {
            addCalibrationForm(sensor.getIp(), sensor.getPort());
        }

        setupBallsForm();
        setupLogForm();

        //BotControlManager.Companion.get().startBotOperator();
    }

    private void setupLogForm() {
        JPanel a = new LogForm().pRoot;
        pLog.add(a);
    }

    private void setupBallsForm() {
        pBalls.add(new BallsForm().pRoot);
    }

    private void addCalibrationForm(String ip, String port) {
        tpCalibration.add(new CalibrationForm(ip, port).rootPanel);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new Main().pRoot);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
