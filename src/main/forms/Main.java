package main.forms;

import main.sensor.Http;

import javax.swing.*;

public class Main {
    private JPanel rootPanel;
    private JButton setButton1;
    private JButton setButton2;
    private JButton setButton3;
    private JButton setButton4;

    private Main() {
        setButton1.addActionListener(e -> Http.Companion.calibrateRef(1));
        setButton2.addActionListener(e -> Http.Companion.calibrateRef(2));
        setButton3.addActionListener(e -> Http.Companion.calibrateRef(3));
        setButton4.addActionListener(e -> Http.Companion.calibrateRef(4));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new Main().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}
