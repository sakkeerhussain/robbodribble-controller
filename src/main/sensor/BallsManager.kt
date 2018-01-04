package main.sensor

import main.sensor.response.Ball

class BallsManager{
    companion object {
        val balls = ArrayList<BallModel>()
    }
}
data class BallModel(val ball: Ball, var rank: Int, var notFoundCount: Int){
    override fun toString(): String {
        return "$ball, (rank=$rank), (notFoundCount=$notFoundCount)"
    }
}