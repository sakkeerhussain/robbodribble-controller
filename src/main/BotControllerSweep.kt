package main

import main.controllers.BotLocation
import main.controllers.BotLocationManager
import main.geometry.Point

fun main(args: Array<String>) {
    println("Starting...")
    BotControllerSweep().start()
}

class BotControllerSweep: BotLocationManager.Listener {

    private val pathList = ArrayList<Point>()
    private val pathIndex = 0
    private val post = 1

    fun start() {
        if (post == 1) {
            pathList.add(Point(125f, 130f))
            pathList.add(Point(125f, 50f))
        }else{
            pathList.add(Point(155f, 50f))
        }
        BotLocationManager.get().addListener(this)
    }

    override fun botLocationChanged(botLocation: BotLocation?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}