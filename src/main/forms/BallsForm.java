package main.forms;

import main.controllers.bot.BotControllerSweep;
import main.controllers.*;
import main.controllers.bot.BotControlManager;
import main.opencv.OpenCV;
import main.utils.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class BallsForm implements BallsManager.Listener, BotLocationManager.Listener, PathManager.Listener {
    JPanel pRoot;
    private JPanel pBallsDiagram;
    private JList ltBalls;
    private JButton btBotControl;
    private JButton btSweeperControl;
    private JButton btFrameGrab;
    private BallsUI mBallsUI;

    BallsForm() {
        mBallsUI = new BallsUI();
        pBallsDiagram.add(mBallsUI);
        BallsManager.INSTANCE.addListener(this);
        BotLocationManager.INSTANCE.addListener(this);
        PathManager.INSTANCE.addListener(this);

        pRoot.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                super.componentHidden(e);
                BallsManager.INSTANCE.removeListener(BallsForm.this);
                BotLocationManager.INSTANCE.removeListener(BallsForm.this);
            }
        });
        btBotControl.addActionListener(e -> {
            String stopText = "Stop Bot";
            if (btBotControl.getText().equals(stopText)) {
                BotControlManager.INSTANCE.stopBotOperator();
                btBotControl.setText("Start Bot");
            } else {
                BotControlManager.INSTANCE.startBotOperator();
                btBotControl.setText(stopText);
            }
        });
        btSweeperControl.addActionListener(e -> {
            String stopText = "Stop Sweeper";
            if (btSweeperControl.getText().equals(stopText)) {
                BotControllerSweep.INSTANCE.stop();
                btSweeperControl.setText("Start Sweeper");
            } else {
                BotControllerSweep.INSTANCE.start();
                btSweeperControl.setText(stopText);
            }
        });
        btFrameGrab.addActionListener(e -> {
            String stopText = "Stop Frame Grab";
            if (btFrameGrab.getText().equals(stopText)) {
                OpenCV.INSTANCE.stopFrameGrabber();
                btFrameGrab.setText("Start Frame Grab");
            } else {
                OpenCV.INSTANCE.startFrameGrabber();
                btFrameGrab.setText(stopText);
            }
        });
    }

    @Override
    public void ballListChanged(@NotNull List<BallModel> balls) {
        ltBalls.setListData(balls.toArray());
        mBallsUI.setBalls(balls);
        pBallsDiagram.revalidate();
        pBallsDiagram.repaint();
    }

    @Override
    public void botLocationChanged(@Nullable BotLocation botLocation) {
        mBallsUI.setBot(botLocation);
        pBallsDiagram.revalidate();
        pBallsDiagram.repaint();
    }

    @Override
    public void pathChanged(@NotNull Path path) {
        mBallsUI.setPath(path);
        pBallsDiagram.revalidate();
        pBallsDiagram.repaint();
    }
}
