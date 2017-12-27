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

    private Main() {
        setButton1.addActionListener(e -> Http.Companion.calibrateRef(1, lbMessage));
        setButton2.addActionListener(e -> Http.Companion.calibrateRef(2, lbMessage));
        setButton3.addActionListener(e -> Http.Companion.calibrateRef(3, lbMessage));
        setButton4.addActionListener(e -> Http.Companion.calibrateRef(4, lbMessage));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new Main().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}
