package main.sensor.response

import main.controllers.BotLocation

data class BotLocationResponse(val data:BotLocation): BaseResponse(null, null)
