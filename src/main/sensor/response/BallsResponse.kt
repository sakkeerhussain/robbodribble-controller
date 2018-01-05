package main.sensor.response

import main.controllers.Ball

data class BallsResponse(val data:List<Ball>): BaseResponse(null, null)
