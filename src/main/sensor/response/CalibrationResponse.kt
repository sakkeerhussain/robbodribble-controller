package main.sensor.response

import main.geometry.Point

data class CalibrationResponse(val data: CalibrationResponseData) : BaseResponse(null, null)
data class CalibrationResponseData(val pointBoard: Point, val pointImage: Point)