package main.forms;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LogForm {
    private JTextArea taLogs;
    JPanel pRoot;
    private JButton clearButton;
    public static Listener logger;

    LogForm(){
        logger = new Listener(taLogs);
        clearButton.addActionListener(e -> taLogs.setText(""));
    }

    public class Listener{
        private JTextArea taLogs;

        Listener(JTextArea ta) {
            taLogs = ta;
        }

        public void println(@NotNull String msg) {
            taLogs.append("\n"+msg);
        }
    }
}
