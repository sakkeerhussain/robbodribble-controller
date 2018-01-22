package main

import main.controllers.BotLocation
import main.controllers.BotLocationManager
import main.controllers.Const
import main.controllers.bot.BotCommunicationService
import main.controllers.bot.BotControlManager
import main.controllers.bot.PathRequestItem
import main.forms.LogForm
import main.geometry.Line
import main.geometry.Point
import java.util.concurrent.Executors

fun main(args: Array<String>) {
    println("Initialising...")
    when {
        args[0] == "post-1" -> BotControllerSweep().start(1)
        args[0] == "post-2" -> BotControllerSweep().start(2)
        else -> println("Post not specified")
    }

}

class BotControllerSweep : BotLocationManager.Listener {

    private val pathList = ArrayList<Point>()
    private var pathIndex = 0
    private var moveStartPoint: Point? = null

    fun start(post: Int) {
        if (post == 1) {
            //Center to post
            pathList.add(Point(125f, 130f))
            pathList.add(Point(155f, 50f))
            pathList.add(Point(50f, 50f))
            pathList.add(Point(20f, 90f))
            pathList.add(Point(50f, 90f))
            pathList.add(Point(20f, 90f))
            pathList.add(Point(-1f, -1f)) //Open Door

            //Sweep 1
            pathList.add(Point(30f, 105f))
            pathList.add(Point(250f, 105f))
            pathList.add(Point(250f, 135f))
            pathList.add(Point(30f, 135f))
            pathList.add(Point(20f, 90f))
            pathList.add(Point(50f, 90f))
            pathList.add(Point(20f, 90f))
            pathList.add(Point(-1f, -1f)) //Open Door

            //Sweep 2
            pathList.add(Point(30f, 75f))
            pathList.add(Point(250f, 75f))
            pathList.add(Point(250f, 45f))
            pathList.add(Point(30f, 45f))
            pathList.add(Point(20f, 90f))
            pathList.add(Point(50f, 90f))
            pathList.add(Point(20f, 90f))
            pathList.add(Point(-1f, -1f)) //Open Door


            println("Starting...")
        } else {
            pathList.add(Point(155f, 50f))
        }
        moveStartPoint = pathList[pathIndex]
        BotLocationManager.get().addListener(this)
    }

    override fun botLocationChanged(botLocation: BotLocation?) {
        if (botLocation == null)
            return
        println("========Bot Location received(${botLocation.point()})=======")

        when {
            botLocation.point().isAt(pathList[pathIndex], Const.BOT_ALLOWED_DEVIATION) -> {
                moveStartPoint = botLocation.point()
                pathIndex++
                if (pathList[pathIndex].x == -1f) {
                    sendDoorOpenToBot()
                    Thread.sleep(5000)
                }
                createPathToNextPoint()

            }
            botLocation.point().isOnLine(Line(Const.POST_LOCATION, moveStartPoint!!), Const.BOT_ALLOWED_DEVIATION) -> {

                if (Line(botLocation.point(), moveStartPoint!!).length() < Const.BOT_MIN_DIST_IN_UNIT_TIME) {
                    sendStopToBot()
                    createPathToNextPoint()
                } else {
                    println("Bot reached at ${botLocation.point()}")
                    moveStartPoint = botLocation.point()
                }
            }

        }
    }

    private fun createPathToNextPoint(){
        val point = pathList[pathIndex]
        TODO("sdgsgsg")
    }

    private fun sendDoorOpenToBot() {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - DOOR OPEN").doorOpen()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            println("Sent door open to bot successfully")
                        }
                    }, {error->
                        println("Sent door open to bot failed, message:${error.localizedMessage}")
                    })
        })
    }

    private fun sendPathToBot(pathList: ArrayList<PathRequestItem>) {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - PATH").sendPath(pathList)
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            println("Sent path to bot successfully")
                        }
                    }, { error ->
                        println("Sent path to bot failed, message:${error.localizedMessage}")
                    })
        })
    }

    private fun sendStopToBot() {
        println("Sending stop to bot...")
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - STOP").stop()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            println("Sent stop to bot successfully")
                        }
                    }, {error->
                        println("Sent stop to bot failed, message:${error.localizedMessage}")
                    })
        })
    }

}