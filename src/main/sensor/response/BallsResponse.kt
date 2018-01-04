package main.sensor.response

import main.sensor.Ball

data class BallsResponse(val data:List<Ball>): BaseResponse(null, null)
