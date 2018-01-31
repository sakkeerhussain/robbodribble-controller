package main.sensor

import main.controllers.Ball
import main.controllers.BotLocation


interface BallsListListener {
    fun ballsListReceived(ip: String, port: String, data: List<Ball>?)
    fun ballsListFailed(ip: String, port: String)
}

interface BotLocationListener {
    fun botLocationReceived(ip: String, port: String, data: BotLocation?)
    fun botLocationFailed(ip: String, port: String)
}

interface OpponentLocationListener {
    fun opponentLocationReceived(ip: String, port: String, data: List<Ball>)
    fun opponentLocationFailed(ip: String, port: String)
}