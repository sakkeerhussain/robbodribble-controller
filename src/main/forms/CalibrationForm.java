package main.forms;

import main.sensor.Http;

import javax.swing.*;
import java.util.concurrent.Executors;

public class CalibrationForm {
    private final String mIp;
    JPanel rootPanel;
    private JButton detButton1;
    private JButton detButton2;
    private JButton detButton3;
    private JButton detButton4;
    private JLabel lbMessage;
    private JTextField tfPoint1x;
    private JTextField tfPoint1y;
    private JTextField tfPoint2x;
    private JTextField tfPoint2y;
    private JTextField tfPoint3x;
    private JTextField tfPoint3y;
    private JTextField tfPoint4x;
    private JTextField tfPoint4y;
    private JButton setButton1;
    private JButton setButton3;
    private JButton setButton2;
    private JButton setButton4;
    private JButton btRefresh;
    private JButton btClear;

    CalibrationForm(String ip) {
        mIp = ip;
        rootPanel.setName(ip);
        detButton1.addActionListener(e -> {
            Http.Companion.calibrateRef(mIp, 1, lbMessage, () -> updateRefPointData(1));
        });
        detButton2.addActionListener(e -> {
            Http.Companion.calibrateRef(mIp, 2, lbMessage, () -> updateRefPointData(2));
        });
        detButton3.addActionListener(e -> {
            Http.Companion.calibrateRef(mIp, 3, lbMessage, () -> updateRefPointData(3));
        });
        detButton4.addActionListener(e -> {
            Http.Companion.calibrateRef(mIp, 4, lbMessage, () -> updateRefPointData(4));
        });
        setButton1.addActionListener(e -> {
            float xImage = Float.valueOf(tfPoint1x.getText());
            float yImage = Float.valueOf(tfPoint1y.getText());
            Http.Companion.setReferencePoint(mIp, 1, lbMessage, xImage, yImage, 0, 0, () -> updateRefPointData(1));
        });
        setButton2.addActionListener(e -> {
            float xImage = Float.valueOf(tfPoint2x.getText());
            float yImage = Float.valueOf(tfPoint2y.getText());
            Http.Companion.setReferencePoint(mIp, 2, lbMessage, xImage, yImage, 180, 0, () -> updateRefPointData(2));
        });
        setButton3.addActionListener(e -> {
            float xImage = Float.valueOf(tfPoint3x.getText());
            float yImage = Float.valueOf(tfPoint3y.getText());
            Http.Companion.setReferencePoint(mIp, 3, lbMessage, xImage, yImage, 0, 280, () -> updateRefPointData(3));
        });
        setButton4.addActionListener(e -> {
            float xImage = Float.valueOf(tfPoint4x.getText());
            float yImage = Float.valueOf(tfPoint4y.getText());
            Http.Companion.setReferencePoint(mIp, 4, lbMessage, xImage, yImage, 180, 280, () -> updateRefPointData(4));
        });
        btClear.addActionListener(e ->{
            lbMessage.setText("Messages");
        });
        btRefresh.addActionListener(e ->{
            updateRefPointData(-1);
        });
        updateRefPointData(-1);
    }

    private void updateRefPointData(int point) {
        if (point == 1 || point == -1)
            Http.Companion.getReferencePoint(mIp, 1, lbMessage, tfPoint1x, tfPoint1y);
        if (point == 2 || point == -1)
            Http.Companion.getReferencePoint(mIp, 2, lbMessage, tfPoint2x, tfPoint2y);
        if (point == 3 || point == -1)
            Http.Companion.getReferencePoint(mIp, 3, lbMessage, tfPoint3x, tfPoint3y);
        if (point == 4 || point == -1)
            Http.Companion.getReferencePoint(mIp, 4, lbMessage, tfPoint4x, tfPoint4y);
    }
}
