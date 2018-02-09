package main.forms;

import main.opencv.OpenCV;
import main.sensor.Sensor;
import main.sensor.SensorsManager;
import main.utils.Log;

import javax.swing.*;
import java.util.Date;

public class Main {
    private static String TAG = "Main";
    private JTabbedPane tabbedPane1;
    private JPanel pRoot;
    private JTabbedPane tpCalibration;
    private JPanel pBalls;
    private JPanel pLog;
    private JPanel pLocalCalibration;

    private Main() {
        //BotControlManager.Companion.get().startBotOperator();
        Log.Companion.d("", "\n\n****************************************************"
                + "\nStarting application at " + new Date()
                + "\n****************************************************");


        for (Sensor sensor : SensorsManager.INSTANCE.getSensorsList()) {
            addCalibrationForm(sensor.getIp(), sensor.getPort());
        }

        setupBallsForm();
        setupLogForm();
        initializingOpenCV();
        setupLocalCalibrationForm();
    }

    private void initializingOpenCV() {
        OpenCV.INSTANCE.init();
        Sensor sensor = SensorsManager.INSTANCE.getSensorsList().get(0);
        OpenCV.INSTANCE.setCamUrl(sensor.getImageUrl());
    }

    private void setupLogForm() {
        JPanel a = new LogForm().pRoot;
        pLog.add(a);
    }

    private void setupBallsForm() {
        pBalls.add(new BallsForm().pRoot);
    }

    private void setupLocalCalibrationForm() {
        pLocalCalibration.add(new CalibLocForm().pRoot);
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
