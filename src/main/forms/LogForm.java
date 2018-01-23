package main.forms;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LogForm {
    private JTextArea taLogs;
    JPanel pRoot;
    private JButton clearButton;
    private JTextField tfSearch;
    private JScrollPane spLogs;
    public static Listener logger = new Listener();

    LogForm() {
        logger.setViewObjects(taLogs, spLogs);
        clearButton.addActionListener(e -> logger.clearLogs());

        //Setting up search form
        tfSearch.setForeground(Color.GRAY);
        tfSearch.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tfSearch.getText().equals("Search")) {
                    tfSearch.setText("");
                    tfSearch.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (tfSearch.getText().isEmpty()) {
                    tfSearch.setForeground(Color.GRAY);
                    tfSearch.setText("Search");
                }
            }
        });
        tfSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                String search = tfSearch.getText();
                if (search.equals("Search"))
                    search = "";
                logger.setSearchStr(search);
            }
        });
    }

    public static class Listener {
        private JTextArea taLogs;
        private String log;
        private String searchStr;
        private JScrollPane spLogs;

        Listener() {
            log = "";
            searchStr = "";
            taLogs = null;
        }

        public void println(@NotNull String tag, @NotNull String msg) {
            if (null == taLogs)
                System.out.println(tag + " => " + msg);
            else {
                log = log.concat(tag).concat(" => ").concat(msg).concat("\n");
                updateLogText();
            }
        }

        private void setSearchStr(String searchStr) {
            this.searchStr = searchStr;
            updateLogText();
        }

        private void setViewObjects(JTextArea taLogs, JScrollPane spLogs) {
            this.taLogs = taLogs;
            this.spLogs = spLogs;
        }

        private void clearLogs() {
            this.log = "";
            updateLogText();
        }

        private void updateLogText() {
            taLogs.setText(getFilteredLog());
            JScrollBar scrollBar = spLogs.getVerticalScrollBar();
            scrollBar.setValue(scrollBar.getMaximum());
        }

        private String getFilteredLog() {
            StringBuilder stringBuilder = new StringBuilder();
            for (String str : log.split("\n")) {
                if (str.contains(searchStr))
                    stringBuilder.append(str).append("\n");
            }
            return stringBuilder.toString();
        }
    }
}
