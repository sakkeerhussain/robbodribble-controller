package main.sensor.response

data class CalibrationResponse(val data: CalibrationResponseData) : BaseResponse(null, null)
data class CalibrationResponseData(val pointBoard: Point, val pointImage: Point)
data class Point(val x: Float, val y: Float)