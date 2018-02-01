package main.forms;

import javax.swing.*;

public class CalibLocForm {
    JPanel pRoot;
    private JLabel lbMessage;
    private JTextField tfPoint1x;
    private JTextField tfPoint1y;
    private JTextField tfPoint2x;
    private JTextField tfPoint2y;
    private JTextField tfPoint3x;
    private JTextField tfPoint3y;
    private JTextField tfPoint4x;
    private JTextField tfPoint4y;
    private JButton btSet1;
    private JButton btSet3;
    private JButton btSet2;
    private JButton btSet4;
    private JButton btRefresh;
    private JButton btClear;
    private JTextField tfPoint12x;
    private JTextField tfPoint12y;
    private JTextField tfPoint34x;
    private JTextField tfPoint34y;
    private JButton btSet12;
    private JButton btSet34;

    CalibLocForm() {
        btSet1.addActionListener(e -> {
//            float xImage = Float.valueOf(tfPoint1x.getText());
//            float yImage = Float.valueOf(tfPoint1y.getText());
//            Http.Companion.setReferencePoint(mIp, mPort, 1, lbMessage, xImage, yImage, -10, -10, () -> updateRefPointData(1));
        });
        btSet2.addActionListener(e -> {
//            float xImage = Float.valueOf(tfPoint2x.getText());
//            float yImage = Float.valueOf(tfPoint2y.getText());
//            Http.Companion.setReferencePoint(mIp, mPort, 2, lbMessage, xImage, yImage, 290, -10, () -> updateRefPointData(2));
        });
        btSet3.addActionListener(e -> {
//            float xImage = Float.valueOf(tfPoint3x.getText());
//            float yImage = Float.valueOf(tfPoint3y.getText());
//            Http.Companion.setReferencePoint(mIp, mPort, 3, lbMessage, xImage, yImage, -10, 190, () -> updateRefPointData(3));
        });
        btSet4.addActionListener(e -> {
//            float xImage = Float.valueOf(tfPoint4x.getText());
//            float yImage = Float.valueOf(tfPoint4y.getText());
//            Http.Companion.setReferencePoint(mIp, mPort, 4, lbMessage, xImage, yImage, 290, 190, () -> updateRefPointData(4));
        });
        btSet12.addActionListener(e -> {
//            float xImage = Float.valueOf(tfPoint4x.getText());
//            float yImage = Float.valueOf(tfPoint4y.getText());
//            Http.Companion.setReferencePoint(mIp, mPort, 4, lbMessage, xImage, yImage, 290, 190, () -> updateRefPointData(4));
        });
        btSet34.addActionListener(e -> {
//            float xImage = Float.valueOf(tfPoint4x.getText());
//            float yImage = Float.valueOf(tfPoint4y.getText());
//            Http.Companion.setReferencePoint(mIp, mPort, 4, lbMessage, xImage, yImage, 290, 190, () -> updateRefPointData(4));
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
//        if (point == 1 || point == -1)
//            Http.Companion.getReferencePoint(mIp, mPort,1, lbMessage, tfPoint1x, tfPoint1y);
//        if (point == 2 || point == -1)
//            Http.Companion.getReferencePoint(mIp, mPort,2, lbMessage, tfPoint2x, tfPoint2y);
//        if (point == 3 || point == -1)
//            Http.Companion.getReferencePoint(mIp, mPort,3, lbMessage, tfPoint3x, tfPoint3y);
//        if (point == 4 || point == -1)
//            Http.Companion.getReferencePoint(mIp, mPort,4, lbMessage, tfPoint4x, tfPoint4y);
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Main");
//        frame.setContentPane(new CalibLocForm().pRoot);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//    }
}
