package main.controllers

import main.sensor.BallsListResponse
import main.sensor.Http
import kotlin.math.absoluteValue


class BallsManager : BallsListResponse {
    companion object {
        val BALL_LOCATION_TOLERANCE = 3
        val BALL_NOT_FOUND_TOLERANCE = 2
        private var instance: BallsManager? = null

        fun get(): BallsManager {
            if (instance == null)
                instance = BallsManager()
            return instance!!
        }
    }
    private var listeners = ArrayList<Listener>()
    fun addListener(listener: Listener){
        if (listener !in listeners)
            listeners.add(listener)
        listener.ballListChanged(ballList)
    }

    fun removeListener(listener: Listener){
        listeners.remove(listener)
    }

    private fun notifyListeners(){
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

    override fun ballsListReceived(ip: String, data: List<Ball>) {
        updateBallsList(data)
        getBallsList(ip)
    }

    override fun ballsListFailed(ip: String) {
        getBallsList(ip)
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

data class Ball(val x: Float, val y: Float) {
    override fun toString(): String {
        return "X: $x, Y:$y"
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ball

        if ((x - other.x).absoluteValue > BallsManager.BALL_LOCATION_TOLERANCE) return false
        if ((y - other.y).absoluteValue > BallsManager.BALL_LOCATION_TOLERANCE) return false

        return true
    }
}