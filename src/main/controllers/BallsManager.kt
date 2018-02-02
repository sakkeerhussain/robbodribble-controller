package main.controllers

import main.geometry.Line
import main.geometry.Point
import main.sensor.BallsListListener
import main.sensor.Http
import main.sensor.SensorsManager


class BallsManager : BallsListListener {
    companion object {
        val BALL_LOCATION_TOLERANCE = 6
        val BALL_NOT_FOUND_TOLERANCE = 1
        private var instance: BallsManager? = null

        fun get(): BallsManager {
            if (instance == null)
                instance = BallsManager()
            return instance!!
        }
    }

    private var listeners = ArrayList<Listener>()
    fun addListener(listener: Listener) {
        if (listener !in listeners)
            listeners.add(listener)
        //listener.ballListChanged(ballList)
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

    fun updateBallsList(balls: List<Ball>?) {
        ballList.forEach { it.present = false }
        if (balls != null)
            balls.forEach { ball -> detectBallFromList(ball) }
        ballList.filter { !it.present }.forEach {
            if (it.notFoundCount >= BALL_NOT_FOUND_TOLERANCE) {
                ballList.remove(it)
            } else {
                it.notFoundCount++
            }
        }
        notifyListeners()
    }

    private fun detectBallFromList(ball: Ball) {
        ballList
                .filter { it.ball == ball }
                .forEach {
                    it.present = true
                    it.rank++
                    it.notFoundCount = 0
                    return
                }
        val ballModel = BallModel(ball, 1, 0, true, 0.0, 0.0)
        ballList.add(ballModel)
    }

    fun startBallsRequestForAllSensors() {
        for (sensor in SensorsManager.get().getSensorsList()) {
            Runnable { getBallsList(sensor.ip, sensor.port) }.run()
        }
    }

    private fun getBallsList(ip: String, port: String) {
        Http.getBalls(ip, port, null, this)
    }

    private fun sortBallsListAccordingToRank(): List<BallModel> {
        ballList.forEach { it.calculateRank() }
        ballList.sortBy { it.rank }
        return ballList
    }

    fun getRankOneBall(): BallModel? {
        val ballListRanked = sortBallsListAccordingToRank()
        return if (ballListRanked.isEmpty()) null else ballListRanked[0]
    }

    override fun ballsListReceived(ip: String, port: String, data: List<Ball>?) {
        updateBallsList(data)
        Thread.sleep(1000)
        getBallsList(ip, port)
    }

    override fun ballsListFailed(ip: String, port: String) {
        updateBallsList(null);
    }

    interface Listener {
        fun ballListChanged(balls: List<BallModel>)
    }
}

data class BallModel(val ball: Ball, var sensorRank: Int, var notFoundCount: Int,
                     var present: Boolean, var rank: Double, var angleToBot: Double) {
    override fun toString(): String {
        return "$ball, r=$rank, sr=$sensorRank, nfc=$notFoundCount"
    }

    fun calculateRank(): Double {
        val botLocation = BotLocationManager.get().getBotLocation() ?: return 0.0;
        val botFrontCenterToBallLine = Line(botLocation.frontSide().mid(), this.ball.center)
        val distancePoint = botFrontCenterToBallLine.length() * Const.BALL_RANK_DISTANCE_CONSTANT
        angleToBot = (botFrontCenterToBallLine.angleInDegree() - botLocation.angle)
        val anglePoint = angleToBot * Const.BALL_RANK_ANGLE_CONSTANT
        val sensorPoint = sensorRank * Const.BALL_RANK_SENSOR_CONSTANT
        rank = distancePoint + anglePoint + sensorPoint
        return rank
    }
}

data class Ball(val center: Point) {
    override fun toString(): String {
        return "(%.2f, %.2f)".format(center.x, center.y)
    }

    override fun hashCode(): Int {
        return center.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ball

        if (center.distanceTo(other.center) > BallsManager.BALL_LOCATION_TOLERANCE) return false

        return true
    }
}