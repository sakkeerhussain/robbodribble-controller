package main.sensor.response

data class BallsResponse(val data:List<Ball>): BaseResponse(null, null)

data class Ball(val x: Int, val y: Int)
