package main.forms;

import main.opencv.OpenCV;
import main.opencv.OpenCvUtils;
import main.opencv.models.ReferencePoint;
import main.utils.ImageToRealMapper;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CalibLocForm {
    private static final int OB_ADJUST_VAL = 30;
    JPanel pRoot;
    private JTextField tfPoint1x;
    private JTextField tfPoint1y;
    private JTextField tfPoint2x;
    private JTextField tfPoint2y;
    private JTextField tfPoint3x;
    private JTextField tfPoint3y;
    private JTextField tfPoint4x;
    private JTextField tfPoint4y;
    private JTextField tfPoint12x;
    private JTextField tfPoint12y;
    private JTextField tfPoint34x;
    private JTextField tfPoint34y;
    private JButton btSet1;
    private JButton btSet2;
    private JButton btSet3;
    private JButton btSet4;
    private JButton btSet12;
    private JButton btSet34;
    private JButton btRefresh;
    private JLabel jlPreview;
    private JTextField tfPointCx;
    private JTextField tfPointCy;
    private JTextField tfPointQ1x;
    private JTextField tfPointQ1y;
    private JTextField tfPointQ2x;
    private JTextField tfPointQ2y;
    private JButton btSetC;
    private JButton btSetQ1;
    private JButton btSetQ2;
    private JTextField tfPointOB1x;
    private JTextField tfPointOB1y;
    private JTextField tfPointOB2x;
    private JTextField tfPointOB3x;
    private JTextField tfPointOB4x;
    private JTextField tfPointOB4y;
    private JTextField tfPointOB3y;
    private JTextField tfPointOB2y;
    private JButton btSetOB1;
    private JButton btSetOB2;
    private JButton btSetOB3;
    private JButton btSetOB4;
    private JPanel jpRefTop;
    private JPanel jpRefBottom;
    private JPanel jpRefCenter;
    private JPanel jpRefOuterBorder;

    CalibLocForm() {
        btSet1.addActionListener(e -> {
            float xImage = Float.valueOf(tfPoint1x.getText());
            float yImage = Float.valueOf(tfPoint1y.getText());
            OpenCV.INSTANCE.getBoardReference().setRefPoint1(new ReferencePoint(xImage, yImage, 0.01f, 0.01f));
            OpenCV.INSTANCE.getBoardReference().setRefPointOB1(new ReferencePoint(xImage-OB_ADJUST_VAL, yImage-OB_ADJUST_VAL, 0.01f, 0.01f));
            ImageToRealMapper.INSTANCE.updateMappingConstants();
            updateRefPointData(1);
        });
        btSet2.addActionListener(e -> {
            float xImage = Float.valueOf(tfPoint2x.getText());
            float yImage = Float.valueOf(tfPoint2y.getText());
            OpenCV.INSTANCE.getBoardReference().setRefPoint2(new ReferencePoint(xImage, yImage, 280f, 0.01f));
            OpenCV.INSTANCE.getBoardReference().setRefPointOB2(new ReferencePoint(xImage+OB_ADJUST_VAL, yImage-OB_ADJUST_VAL, 280f, 0.01f));
            ImageToRealMapper.INSTANCE.updateMappingConstants();
            updateRefPointData(2);
        });
        btSet3.addActionListener(e -> {
            float xImage = Float.valueOf(tfPoint3x.getText());
            float yImage = Float.valueOf(tfPoint3y.getText());
            OpenCV.INSTANCE.getBoardReference().setRefPoint3(new ReferencePoint(xImage, yImage, 0.01f, 180f));
            OpenCV.INSTANCE.getBoardReference().setRefPointOB3(new ReferencePoint(xImage-OB_ADJUST_VAL, yImage+OB_ADJUST_VAL, 0.01f, 180f));
            ImageToRealMapper.INSTANCE.updateMappingConstants();
            updateRefPointData(3);
        });
        btSet4.addActionListener(e -> {
            float xImage = Float.valueOf(tfPoint4x.getText());
            float yImage = Float.valueOf(tfPoint4y.getText());
            OpenCV.INSTANCE.getBoardReference().setRefPoint4(new ReferencePoint(xImage, yImage, 280f, 180f));
            OpenCV.INSTANCE.getBoardReference().setRefPointOB4(new ReferencePoint(xImage+OB_ADJUST_VAL, yImage+OB_ADJUST_VAL, 280f, 180f));
            ImageToRealMapper.INSTANCE.updateMappingConstants();
            updateRefPointData(4);
        });
        btSet12.addActionListener(e -> {
            float xImage = Float.valueOf(tfPoint12x.getText());
            float yImage = Float.valueOf(tfPoint12y.getText());
            OpenCV.INSTANCE.getBoardReference().setRefPointMid12(new ReferencePoint(xImage, yImage, 140f, -10f));
            ImageToRealMapper.INSTANCE.updateMappingConstants();
            updateRefPointData(12);
        });
        btSet34.addActionListener(e -> {
            float xImage = Float.valueOf(tfPoint34x.getText());
            float yImage = Float.valueOf(tfPoint34y.getText());
            OpenCV.INSTANCE.getBoardReference().setRefPointMid34(new ReferencePoint(xImage, yImage, 140f, 190f));
            ImageToRealMapper.INSTANCE.updateMappingConstants();
            updateRefPointData(34);
        });
        btSetC.addActionListener(e -> {
            float xImage = Float.valueOf(tfPointCx.getText());
            float yImage = Float.valueOf(tfPointCy.getText());
            OpenCV.INSTANCE.getBoardReference().setRefPointC(new ReferencePoint(xImage, yImage, 140f, 90f));
            ImageToRealMapper.INSTANCE.updateMappingConstants();
            updateRefPointData(5);
        });
        btSetQ1.addActionListener(e -> {
            float xImage = Float.valueOf(tfPointQ1x.getText());
            float yImage = Float.valueOf(tfPointQ1y.getText());
            OpenCV.INSTANCE.getBoardReference().setRefPointQ1(new ReferencePoint(xImage, yImage, 70f, 90f));
            ImageToRealMapper.INSTANCE.updateMappingConstants();
            updateRefPointData(6);
        });
        btSetQ2.addActionListener(e -> {
            float xImage = Float.valueOf(tfPointQ2x.getText());
            float yImage = Float.valueOf(tfPointQ2y.getText());
            OpenCV.INSTANCE.getBoardReference().setRefPointQ2(new ReferencePoint(xImage, yImage, 210f, 90f));
            ImageToRealMapper.INSTANCE.updateMappingConstants();
            updateRefPointData(7);
        });
        btSetOB1.addActionListener(e -> {
            float xImage = Float.valueOf(tfPointOB1x.getText());
            float yImage = Float.valueOf(tfPointOB1y.getText());
            OpenCV.INSTANCE.getBoardReference().setRefPointOB1(new ReferencePoint(xImage, yImage, -10f, -10f));
            ImageToRealMapper.INSTANCE.updateMappingConstants();
            updateRefPointData(8);
        });
        btSetOB2.addActionListener(e -> {
            float xImage = Float.valueOf(tfPointOB2x.getText());
            float yImage = Float.valueOf(tfPointOB2y.getText());
            OpenCV.INSTANCE.getBoardReference().setRefPointOB2(new ReferencePoint(xImage, yImage, 290f, -10f));
            ImageToRealMapper.INSTANCE.updateMappingConstants();
            updateRefPointData(9);
        });
        btSetOB3.addActionListener(e -> {
            float xImage = Float.valueOf(tfPointOB3x.getText());
            float yImage = Float.valueOf(tfPointOB3y.getText());
            OpenCV.INSTANCE.getBoardReference().setRefPointOB3(new ReferencePoint(xImage, yImage, -10f, 190f));
            ImageToRealMapper.INSTANCE.updateMappingConstants();
            updateRefPointData(10);
        });
        btSetOB4.addActionListener(e -> {
            float xImage = Float.valueOf(tfPointOB4x.getText());
            float yImage = Float.valueOf(tfPointOB4y.getText());
            OpenCV.INSTANCE.getBoardReference().setRefPointOB4(new ReferencePoint(xImage, yImage, 290f, 190f));
            ImageToRealMapper.INSTANCE.updateMappingConstants();
            updateRefPointData(11);
        });
        btRefresh.addActionListener(e -> {
            updateRefPointData(-1);
        });
        updateRefPointData(-1);

        jpRefTop.setBorder(BorderFactory.createLineBorder(Color.black));
        jpRefBottom.setBorder(BorderFactory.createLineBorder(Color.black));
        jpRefCenter.setBorder(BorderFactory.createLineBorder(Color.black));
        jpRefOuterBorder.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    private void updateRefPointData(int point) {
        if (point == 1 || point == -1) {
            tfPoint1x.setText(OpenCV.INSTANCE.getBoardReference().getRefPoint1().getPointImage().getX() + "");
            tfPoint1y.setText(OpenCV.INSTANCE.getBoardReference().getRefPoint1().getPointImage().getY() + "");
        }
        if (point == 12 || point == -1) {
            tfPoint12x.setText(OpenCV.INSTANCE.getBoardReference().getRefPointMid12().getPointImage().getX() + "");
            tfPoint12y.setText(OpenCV.INSTANCE.getBoardReference().getRefPointMid12().getPointImage().getY() + "");
        }
        if (point == 2 || point == -1) {
            tfPoint2x.setText(OpenCV.INSTANCE.getBoardReference().getRefPoint2().getPointImage().getX() + "");
            tfPoint2y.setText(OpenCV.INSTANCE.getBoardReference().getRefPoint2().getPointImage().getY() + "");
        }
        if (point == 3 || point == -1) {
            tfPoint3x.setText(OpenCV.INSTANCE.getBoardReference().getRefPoint3().getPointImage().getX() + "");
            tfPoint3y.setText(OpenCV.INSTANCE.getBoardReference().getRefPoint3().getPointImage().getY() + "");
        }
        if (point == 34 || point == -1) {
            tfPoint34x.setText(OpenCV.INSTANCE.getBoardReference().getRefPointMid34().getPointImage().getX() + "");
            tfPoint34y.setText(OpenCV.INSTANCE.getBoardReference().getRefPointMid34().getPointImage().getY() + "");
        }
        if (point == 4 || point == -1) {
            tfPoint4x.setText(OpenCV.INSTANCE.getBoardReference().getRefPoint4().getPointImage().getX() + "");
            tfPoint4y.setText(OpenCV.INSTANCE.getBoardReference().getRefPoint4().getPointImage().getY() + "");
        }
        if (point == 5 || point == -1) {
            tfPointCx.setText(OpenCV.INSTANCE.getBoardReference().getRefPointC().getPointImage().getX() + "");
            tfPointCy.setText(OpenCV.INSTANCE.getBoardReference().getRefPointC().getPointImage().getY() + "");
        }
        if (point == 6 || point == -1) {
            tfPointQ1x.setText(OpenCV.INSTANCE.getBoardReference().getRefPointQ1().getPointImage().getX() + "");
            tfPointQ1y.setText(OpenCV.INSTANCE.getBoardReference().getRefPointQ1().getPointImage().getY() + "");
        }
        if (point == 7 || point == -1) {
            tfPointQ2x.setText(OpenCV.INSTANCE.getBoardReference().getRefPointQ2().getPointImage().getX() + "");
            tfPointQ2y.setText(OpenCV.INSTANCE.getBoardReference().getRefPointQ2().getPointImage().getY() + "");
        }
        if (point == 8 || point == 1 || point == -1) {
            tfPointOB1x.setText(OpenCV.INSTANCE.getBoardReference().getRefPointOB1().getPointImage().getX() + "");
            tfPointOB1y.setText(OpenCV.INSTANCE.getBoardReference().getRefPointOB1().getPointImage().getY() + "");
        }
        if (point == 9 || point == 2 || point == -1) {
            tfPointOB2x.setText(OpenCV.INSTANCE.getBoardReference().getRefPointOB2().getPointImage().getX() + "");
            tfPointOB2y.setText(OpenCV.INSTANCE.getBoardReference().getRefPointOB2().getPointImage().getY() + "");
        }
        if (point == 10 || point == 3 || point == -1) {
            tfPointOB3x.setText(OpenCV.INSTANCE.getBoardReference().getRefPointOB3().getPointImage().getX() + "");
            tfPointOB3y.setText(OpenCV.INSTANCE.getBoardReference().getRefPointOB3().getPointImage().getY() + "");
        }
        if (point == 11 || point == 4 || point == -1) {
            tfPointOB4x.setText(OpenCV.INSTANCE.getBoardReference().getRefPointOB4().getPointImage().getX() + "");
            tfPointOB4y.setText(OpenCV.INSTANCE.getBoardReference().getRefPointOB4().getPointImage().getY() + "");
        }
        drawFrameToLabel();
        if (point != -1) {
            OpenCvUtils.INSTANCE.saveRefPointsToFile();
        }
    }

    private void drawFrameToLabel() {
        try {
            Mat frame = OpenCV.INSTANCE.getFrame();
            if (frame == null || frame.rows() == 0|| frame.cols() == 0
                    || jlPreview.getWidth() == 0 || jlPreview.getHeight() == 0) {
                jlPreview.setText("Unable to print frame now");
                return;
            }
            jlPreview.setText("");
            OpenCvUtils.INSTANCE.drawBordToFrame(frame);
            Imgproc.resize(frame, frame, new Size(jlPreview.getWidth(), jlPreview.getHeight()));
            BufferedImage buffImage = OpenCvUtils.INSTANCE.mat2BufferedImage(frame);
            jlPreview.setIcon(new ImageIcon(buffImage));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
