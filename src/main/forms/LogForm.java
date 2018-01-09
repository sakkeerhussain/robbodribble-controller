package main.forms;

import main.sensor.HttpLoggingInterceptor;
import main.sensor.LoggingListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogForm implements LoggingListener {
    private JTextArea taLogs;
    JPanel pRoot;
    private JButton clearButton;

    LogForm(){
        HttpLoggingInterceptor.Companion.setListener(this);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                taLogs.setText("");
            }
        });
    }

    @Override
    public void log(@NotNull String msg) {
        taLogs.append("\n"+msg);
    }
}
