package main.sensor.response

data class CalibrationResponse(val data: CalibrationResponseData) : BaseResponse(null, null)
data class CalibrationResponseData(val value: Point)
data class Point(val x: Double, val y: Double)