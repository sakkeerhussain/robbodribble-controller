package main.forms;

import main.sensor.*;
import main.sensor.response.Ball;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class BallsForm implements BallsListResponse {
    JPanel pRoot;
    private JButton btRefresh;
    private JPanel pBallsDiagram;
    private JList ltBalls;
    private BallsUI mBallsUI;

    BallsForm(){
        mBallsUI = new BallsUI();
        pBallsDiagram.add(mBallsUI);
        startBallsRequestForAllSensors();
        btRefresh.addActionListener(e -> {
            startBallsRequestForAllSensors();
        });
    }

    private void startBallsRequestForAllSensors() {
        for (Sensor sensor: SensorsManager.Companion.getSENSORS_LIST()) {
            getBallsList(sensor.getIp());
        }
    }

    private void getBallsList(String ip){
        Http.Companion.getBalls(ip, null, this);
    }

    private void updateBallsList(List<Ball> balls){
        ArrayList<BallModel> ballModels = new ArrayList<>();
        for (Ball ball: balls){
            ballModels.add(new BallModel(ball, 1, 0));
        }

        ltBalls.setListData(ballModels.toArray());
        mBallsUI.setBalls(ballModels);
        pBallsDiagram.revalidate();
        pBallsDiagram.repaint();
    }

    @Override
    public void ballsListReceived(@NotNull String ip, @NotNull List<Ball> data) {
        updateBallsList(data);
        getBallsList(ip);
    }

    @Override
    public void ballsListFailed(@NotNull String ip) {
        getBallsList(ip);
//        updateBallsList(null);
    }
}
