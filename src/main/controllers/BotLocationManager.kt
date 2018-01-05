package main.controllers

import main.sensor.BotLocationResponse
import main.sensor.OpponentLocationResponse


class BotLocationManager : BotLocationResponse, OpponentLocationResponse {

    companion object {
        private var instance: BotLocationManager? = null

        fun get(): BotLocationManager {
            if (instance == null)
                instance = BotLocationManager()
            return instance!!
        }
    }

    private var listeners = ArrayList<Listener>()
    fun addListener(listener: Listener) {
        if (listener !in listeners)
            listeners.add(listener)
        listener.ballListChanged(ballList)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it.ballListChanged(ballList) }
    }

    private val ballList = ArrayList<BallModel>()
    fun getBallList(): List<BallModel> {
        return ballList;
    }

    fun updateBotLocation(balls: List<Ball>?) {
        notifyListeners()
    }

//    fun startBallsRequestForAllSensors() {
//        for ((ip) in SensorsManager.SENSORS_LIST) {
//            Runnable { getBallsList(ip) }.run()
//        }
//    }

    //    private fun getBallsList(ip: String) {
//        Http.getBalls(ip, null, this)
//    }
//
//    override fun ballsListReceived(ip: String, data: List<Ball>) {
//        updateBallsList(data)
//        getBallsList(ip)
//    }
//
//    override fun ballsListFailed(ip: String) {
//        getBallsList(ip)
//        updateBallsList(null);
//    }
//
    override fun botLocationReceived(ip: String, data: List<Ball>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun botLocationFailed(ip: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun opponentLocationReceived(ip: String, data: List<Ball>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun opponentLocationFailed(ip: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface Listener {
        fun ballListChanged(balls: List<BallModel>)
    }
}

data class BotModel(val frontLeft: Float, val frontRight: Float, val backLeft: Float, val backRight: Float)