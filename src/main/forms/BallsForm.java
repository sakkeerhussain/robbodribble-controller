package main.forms;

import main.sensor.BallsListResponse;
import main.sensor.Http;
import main.sensor.Sensor;
import main.sensor.SensorsManager;
import main.sensor.response.Ball;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class BallsForm implements BallsListResponse {
    JPanel pRoot;
    private JButton btRefresh;
    private JPanel pBallsDiagram;

    BallsForm(){
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

        ArrayList<Ball> ballsTest = new ArrayList<>();
        ballsTest.add(new Ball(0, 0));
        ballsTest.add(new Ball(180, 0));
        ballsTest.add(new Ball(0, 280));
        ballsTest.add(new Ball(180, 280));
        ballsTest.add(new Ball(50, 50));
        pBallsDiagram.add(new BallsUI(balls));
    }

    @Override
    public void ballsListReceived(@NotNull String ip, @NotNull List<Ball> data) {
        updateBallsList(data);
        getBallsList(ip);
    }

    @Override
    public void ballsListFailed(@NotNull String ip) {
        //getBallsList(ip);
        updateBallsList(null);
    }
}
