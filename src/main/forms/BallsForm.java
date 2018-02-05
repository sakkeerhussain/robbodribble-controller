package main.forms;

import main.controllers.bot.BotControllerSweep;
import main.controllers.*;
import main.controllers.bot.BotControlManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class BallsForm implements BallsManager.Listener, BotLocationManager.Listener {
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
        //BallsManager.Companion.get().startBallsRequestForAllSensors();
        //BotLocationManager.Companion.get().startBotLocationRequestForAllSensors();
        BallsManager.Companion.get().addListener(BallsForm.this);
        BotLocationManager.Companion.get().addListener(BallsForm.this);

        pRoot.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                super.componentHidden(e);
                BallsManager.Companion.get().removeListener(BallsForm.this);
                BotLocationManager.Companion.get().removeListener(BallsForm.this);
            }
        });
        btRefreshBalls.addActionListener(e -> BallsManager.Companion.get().startBallsRequestForAllSensors());
        btRefreshBot.addActionListener(e -> BotLocationManager.Companion.get().startBotLocationRequestForAllSensors());
        btRestartBot.addActionListener(e -> {
            String stopText = "Stop Bot";
            if (btRestartBot.getText().equals(stopText)) {
                BotControlManager.Companion.get().stopBotOperator();
                btStartSweeper.setText("Start Bot");
            } else {
                BotControlManager.Companion.get().startBotOperator();
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

        /*ArrayList<BallModel> list = new ArrayList<>();
        list.add(new BallModel(new Ball(0, 0), 0, 0, false));
        list.add(new BallModel(new Ball(50, 50), 0, 0, false));
        mBallsUI.setBalls(list);*/

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
}
