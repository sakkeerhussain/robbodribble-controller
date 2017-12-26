package main.forms;

import javax.swing.*;

public class Main {
    private JPanel rootPanel;
    private JButton set00Button;
    private JButton set180Button;
    private JButton set028Button;
    private JButton set1828Button;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new Main().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
