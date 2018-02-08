package main.forms;

import main.controllers.bot.BotControllerSweep;
import main.controllers.*;
import main.controllers.bot.BotControlManager;
import main.utils.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.nio.file.PathMatcher;
import java.util.List;

public class BallsForm implements BallsManager.Listener, BotLocationManager.Listener, PathManager.Listener {
    JPanel pRoot;
    private JButton btRefreshBalls;
    private JPanel pBallsDiagram;
    private JList ltBalls;
    private JButton btRefreshBot;
    private JButton btRestartBot;
    private JButton btStartSweeper;
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
        btRefreshBalls.addActionListener(e -> BallsManager.INSTANCE.startBallsRequestForAllSensors());
        btRefreshBot.addActionListener(e -> BotLocationManager.INSTANCE.startBotLocationRequestForAllSensors());
        btRestartBot.addActionListener(e -> {
            String stopText = "Stop Bot";
            if (btRestartBot.getText().equals(stopText)) {
                BotControlManager.INSTANCE.stopBotOperator();
                btStartSweeper.setText("Start Bot");
            } else {
                BotControlManager.INSTANCE.startBotOperator();
                btStartSweeper.setText(stopText);
            }
        });
        btStartSweeper.addActionListener(e -> {
            String stopText = "Stop Sweeper";
            if (btStartSweeper.getText().equals(stopText)) {
                BotControllerSweep.Companion.get().stop();
                btStartSweeper.setText("Start Sweeper");
            } else {
                BotControllerSweep.Companion.get().start();
                btStartSweeper.setText(stopText);
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
