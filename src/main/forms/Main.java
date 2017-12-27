package main.forms;

import main.sensor.Http;

import javax.swing.*;

public class Main {
    private JPanel rootPanel;
    private JButton setButton1;
    private JButton setButton2;
    private JButton setButton3;
    private JButton setButton4;
    private JLabel lbMessage;
    private JLabel lbPoint1;
    private JLabel lbPoint2;
    private JLabel lbPoint3;
    private JLabel lbPoint4;

    private Main() {
        setButton1.addActionListener(e -> {
            Http.Companion.calibrateRef(1, lbMessage);
            updateRefPointData();
        });
        setButton2.addActionListener(e -> {
            Http.Companion.calibrateRef(2, lbMessage);
            updateRefPointData();
        });
        setButton3.addActionListener(e -> {
            Http.Companion.calibrateRef(3, lbMessage);
            updateRefPointData();
        });
        setButton4.addActionListener(e -> {
            Http.Companion.calibrateRef(4, lbMessage);
            updateRefPointData();
        });
        updateRefPointData();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new Main().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void updateRefPointData(){
        Http.Companion.getReferencePoint(1, lbMessage, lbPoint1);
        Http.Companion.getReferencePoint(2, lbMessage, lbPoint2);
        Http.Companion.getReferencePoint(3, lbMessage, lbPoint3);
        Http.Companion.getReferencePoint(4, lbMessage, lbPoint4);
    }


}
