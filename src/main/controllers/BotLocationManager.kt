package main.controllers

import main.geometry.Line
import main.geometry.Point
import main.opencv.OpenCV
import main.opencv.OpenCvUtils
import main.sensor.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors


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
        //listener.botLocationChanged(botLocation)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { Executors.newCachedThreadPool().submit {it.botLocationChanged(botLocation)} }
    }

    private var botLocation: BotLocation? = null
    fun getBotLocation(): BotLocation? {
        return botLocation;
    }

    fun updateBotLocation(botLocation: BotLocation?) {
        this.botLocation = botLocation
        notifyListeners()
    }

    fun invalidateBotLocation() {
        this.botLocation = null
        notifyListeners()
    }

    fun startBotLocationRequestForAllSensors() {
        for (sensor in SensorsManager.get().getSensorsList()) {
            Runnable { getBotLocation(sensor) }.run()
        }
    }

    fun startBotLocationRequestForMainSensor() {
        val sensor = SensorsManager.get().getSensorsList().get(0)
        getBotLocation(sensor)
    }

    private fun getBotLocation(sensor: Sensor) {
        //Http.getBotLocation(ip, port,null, this)
        val botLocation = OpenCvUtils.getBotLocationOnBoard()
        updateBotLocation(botLocation)
    }

    override fun botLocationReceived(ip: String, port: String, data: BotLocation?) {
        updateBotLocation(data)
        //Thread.sleep(1000)
        //getBotLocation(ip, port)
    }

    override fun botLocationFailed(ip: String, port: String) {
        updateBotLocation(null)
    }

    override fun opponentLocationReceived(ip: String, port: String, data: List<Ball>) {
        //TODO have to do
    }

    override fun opponentLocationFailed(ip: String, port: String) {
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

    fun backSide(): Line {
        return Line(backLeft, backRight)
    }

    fun midLine(): Line {
        return Line(backSide().mid(), frontSide().mid())
    }

    fun point(): Point {
        return Line(backSide().mid(), midLine().mid()).mid()
    }
}