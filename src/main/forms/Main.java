package main.forms;

import main.controllers.Sensor;
import main.controllers.SensorsManager;
import main.controllers.bot.BotControlManager;

import javax.swing.*;

public class Main {
    private JTabbedPane tabbedPane1;
    private JPanel pRoot;
    private JTabbedPane tpCalibration;
    private JPanel pBalls;
    private JPanel pLog;

    private Main() {
        for (Sensor sensor: SensorsManager.Companion.get().getSensorsList()) {
            addCalibrationForm(sensor.getIp());
        }

        setupBallsForm();
        setupLogForm();

        BotControlManager.Companion.get().startBotOperator();
    }

    private void setupLogForm() {
        JPanel a = new LogForm().pRoot;
        pLog.add(a);
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
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
