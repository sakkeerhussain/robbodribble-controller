package main.forms;

import main.controllers.BallModel;
import main.controllers.BallsManager;
import main.controllers.BotLocation;
import main.controllers.BotLocationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class BallsForm implements BallsManager.Listener, BotLocationManager.Listener {
    JPanel pRoot;
    private JButton btRefresh;
    private JPanel pBallsDiagram;
    private JList ltBalls;
    private BallsUI mBallsUI;

    BallsForm(){
        mBallsUI = new BallsUI();
        pBallsDiagram.add(mBallsUI);
        BallsManager.Companion.get().startBallsRequestForAllSensors();
        BotLocationManager.Companion.get().startBotLocationRequestForAllSensors();
        btRefresh.addActionListener(e -> {
            BallsManager.Companion.get().startBallsRequestForAllSensors();
            BotLocationManager.Companion.get().startBotLocationRequestForAllSensors();
        });
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
}
