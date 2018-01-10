package main.controllers

import main.geometry.Line
import main.geometry.Point
import main.sensor.BotLocationListener
import main.sensor.Http
import main.sensor.OpponentLocationListener


class BotLocationManager : BotLocationListener, OpponentLocationListener {

    companion object {
        private var instance: BotLocationManager? = null

        fun get(): BotLocationManager {
            if (instance == null)
                instance = BotLocationManager()
            return instance!!
        }
    }

    private var listeners = ArrayList<Listener>()
    fun addListener(listener: Listener) {
        if (listener !in listeners)
            listeners.add(listener)
        listener.botLocationChanged(botLocation)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it.botLocationChanged(botLocation) }
    }

    private var botLocation: BotLocation? = null
    fun getBotLocation(): BotLocation? {
        return botLocation;
    }

    fun updateBotLocation(botLocation: BotLocation?) {
        this.botLocation = botLocation
        notifyListeners()
    }

    fun startBotLocationRequestForAllSensors() {
        for ((ip) in SensorsManager.SENSORS_LIST) {
            Runnable { getBotLocation(ip) }.run()
        }
    }

    private fun getBotLocation(ip: String) {
        Http.getBotLocation(ip, null, this)
    }

    override fun botLocationReceived(ip: String, data: BotLocation?) {
        updateBotLocation(data)
        getBotLocation(ip)
    }

    override fun botLocationFailed(ip: String) {
        updateBotLocation(null)
    }

    override fun opponentLocationReceived(ip: String, data: List<Ball>) {
        //TODO have to do
    }

    override fun opponentLocationFailed(ip: String) {
        //TODO have to do
    }

    interface Listener {
        fun botLocationChanged(botLocation: BotLocation?)
    }
}

data class BotLocation(val angle: Double, val backLeft: Point, val backRight: Point, val frontLeft: Point, val frontRight: Point){
    fun frontSide(): Line {
        return Line(frontLeft, frontRight)
    }
}