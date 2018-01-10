package main.controllers

import main.geometry.Line
import main.geometry.Point
import main.sensor.BallsListListener
import main.sensor.Http


class BallsManager : BallsListListener {
    companion object {
        val BALL_LOCATION_TOLERANCE = 6
        val BALL_NOT_FOUND_TOLERANCE = 2
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
        val ballModel = BallModel(ball, 1, 0, true)
        ballList.add(ballModel)
    }

    fun startBallsRequestForAllSensors() {
        for ((ip) in SensorsManager.SENSORS_LIST) {
            Runnable { getBallsList(ip) }.run()
        }
    }

    private fun getBallsList(ip: String) {
        Http.getBalls(ip, null, this)
    }

    private fun getBallsListRanked(): List<Ball> {
        val result = ArrayList<Ball>()
        if (ballList.isNotEmpty()) {
            //TODO - Complete feature
        }
        return result
    }

    private fun getRankOneBall(): Ball? {
        val ballListRanked = getBallsListRanked()
        return if (ballListRanked.isEmpty()) null else ballListRanked[0]
    }

    override fun ballsListReceived(ip: String, data: List<Ball>?) {
        updateBallsList(data)
        getBallsList(ip)
    }

    override fun ballsListFailed(ip: String) {
        updateBallsList(null);
    }

    interface Listener {
        fun ballListChanged(balls: List<BallModel>)
    }
}

data class BallModel(val ball: Ball, var rank: Int, var notFoundCount: Int, var present: Boolean) {
    override fun toString(): String {
        return "$ball, (rank=$rank), (notFoundCount=$notFoundCount)"
    }
}

data class Ball(val center: Point) {
    override fun toString(): String {
        return "X: ${center.x}, Y:${center.y}"
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

    fun rank(): Double {
        val botLocation = BotLocationManager.get().getBotLocation() ?: return 0.0;
        val distancePoint = botLocation.frontSide().mid().distanceTo(this.center) * Const.BALL_RANK_DISTANCE_CONSTANT
        //TODO  update calculation
        val anglePoint = 0 * Const.BALL_RANK_ANGLE_CONSTANT
        val sensorPoint = 0 * Const.BALL_RANK_SENSOR_CONSTANT
        return distancePoint + anglePoint + sensorPoint;
    }
}