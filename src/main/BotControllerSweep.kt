package main

import main.controllers.BotLocation
import main.controllers.BotLocationManager
import main.controllers.Const
import main.controllers.bot.BotCommunicationService
import main.controllers.bot.PathRequestItem
import main.geometry.Line
import main.geometry.Point
import java.util.concurrent.Executors
import kotlin.math.absoluteValue

fun main(args: Array<String>) {
    println("Initialising...")
    when {
        args[0] == "post-1" -> BotControllerSweep().start(1)
        args[0] == "post-2" -> BotControllerSweep().start(2)
        else -> println("Post not specified")
    }

}

class BotControllerSweep : BotLocationManager.Listener {

    private val pathList = ArrayList<Path>()
    private var pathIndex = 0
    private var moveStartPoint: Point? = null

    fun start(post: Int) {
        if (post == 1) {
            //Center to post
            pathList.add(Path(Point(125f, 130f), true))
            pathList.add(Path(Point(155f, 50f), true))
            pathList.add(Path(Point(50f, 50f), true))
            pathList.add(Path(Point(20f, 90f), true))
            pathList.add(Path(Point(50f, 90f), true))
            pathList.add(Path(Point(20f, 90f), false))
            pathList.add(Path(Point(-1f, -1f), true)) //Open Door

            //Sweep 1
            pathList.add(Path(Point(30f, 105f), true))
            pathList.add(Path(Point(250f, 105f), true))
            pathList.add(Path(Point(250f, 135f), true))
            pathList.add(Path(Point(30f, 135f), true))
            pathList.add(Path(Point(20f, 90f), true))
            pathList.add(Path(Point(50f, 90f), true))
            pathList.add(Path(Point(20f, 90f), false))
            pathList.add(Path(Point(-1f, -1f), true)) //Open Door

            //Sweep 2
            pathList.add(Path(Point(30f, 75f), true))
            pathList.add(Path(Point(250f, 75f), true))
            pathList.add(Path(Point(250f, 45f), true))
            pathList.add(Path(Point(30f, 45f), true))
            pathList.add(Path(Point(20f, 90f), true))
            pathList.add(Path(Point(50f, 90f), true))
            pathList.add(Path(Point(20f, 90f), false))
            pathList.add(Path(Point(-1f, -1f), true)) //Open Door


            println("Starting...")
        } else {
            pathList.add(Path(Point(155f, 50f), true))
        }
        moveStartPoint = pathList[pathIndex].point
        BotLocationManager.get().addListener(this)

        callBotReachedCallBackForTesting(pathList[pathIndex].point)
    }

    override fun botLocationChanged(botLocation: BotLocation?) {
        if (botLocation == null)
            return
        val botLocationPoint = botLocation.point()
        println("========Bot Location received($botLocationPoint)=======")

        when {
            botLocationPoint.isAt(pathList[pathIndex].point, Const.BOT_ALLOWED_DEVIATION) -> {
                moveStartPoint = botLocationPoint
                pathIndex++
                if (pathList[pathIndex].point.x == -1f) {
                    sendDoorOpenToBot()
                    Thread.sleep(5000)
                }
                createPathToPoint(botLocation)

            }
            botLocationPoint.isOnLine(Line(pathList[pathIndex].point, moveStartPoint!!), Const.BOT_ALLOWED_DEVIATION) -> {

                if (Line(botLocationPoint, moveStartPoint!!).length() < Const.BOT_MIN_DIST_IN_UNIT_TIME) {
                    sendStopToBot()
                    createPathToPoint(botLocation)
                } else {
                    println("Bot reached at $botLocationPoint")
                    moveStartPoint = botLocationPoint
                }
            }
            else->{
                val minFrontVal = 120f
                val pointMinFrontH = botLocationPoint.getPointAtAngle(botLocation.midLine().angle(), minFrontVal, true)
                val pointMinFrontL = botLocationPoint.getPointAtAngle(botLocation.midLine().angle(), minFrontVal, true)
                val pointMinFront = if (Line(pointMinFrontH, botLocation.frontSide().mid()).length() < minFrontVal) pointMinFrontH else pointMinFrontL
                val point = pathList[pathIndex].point

                if (Line(pointMinFront, point).length() > Line(botLocationPoint, point).length()){
                    createReversePath(botLocation)
                }else{
                    createPathToPoint(botLocation)
                }
            }

        }
    }

    private fun createPathToPoint(botLocation: BotLocation) {
        val path = pathList[pathIndex]
        val botToPointLine = Line(botLocation.point(), path.point)
        val angle = botLocation.midLine().angleBetween(botToPointLine)
        val ballInFront = Line(botLocation.frontSide().mid(), path.point).length() < Line(botLocation.backSide().mid(), path.point).length()
        val pathList = ArrayList<PathRequestItem>()
        if (path.front) {
            when {
                angle < 0 && ballInFront ->
                    pathList.add(PathRequestItem(Const.PATH_LEFT, angle.absoluteValue.toInt()))
                angle > 0 && ballInFront ->
                    pathList.add(PathRequestItem(Const.PATH_RIGHT, angle.absoluteValue.toInt()))
                angle < 0 && !ballInFront ->
                    pathList.add(PathRequestItem(Const.PATH_RIGHT, (180+angle).absoluteValue.toInt()))
                angle > 0 && !ballInFront ->
                    pathList.add(PathRequestItem(Const.PATH_LEFT, (180-angle).absoluteValue.toInt()))
            }
            pathList.add(PathRequestItem(Const.PATH_FORWARD, botToPointLine.length().toInt()))
        }else{
            //Assuming y value of points in reverse are sa
            pathList.add(PathRequestItem(Const.PATH_BACKWARD, botToPointLine.length().toInt()))
        }
        sendPathToBot(pathList)
//        println("Path list: $pathList")
//        callBotReachedCallBackForTesting(path.point)
    }

    private fun createReversePath(botLocation: BotLocation) {
        val path = pathList[pathIndex]
        val botToPointLine = Line(botLocation.point(), path.point)
        val angle = botLocation.midLine().angleBetween(botToPointLine)
        val pathList = ArrayList<PathRequestItem>()
        if (path.front) {
            when {
                angle < 0 ->
                    pathList.add(PathRequestItem(Const.PATH_RIGHT, 10))
                angle > 0 ->
                    pathList.add(PathRequestItem(Const.PATH_LEFT, 10))
            }
            pathList.add(PathRequestItem(Const.PATH_BACKWARD, 60))
        }
        sendPathToBot(pathList)

//        println("Path list: $pathList")
//        callBotReachedCallBackForTesting(path.point)
    }

    private fun callBotReachedCallBackForTesting(point: Point){
        val backCenter = Point(point.x+7.5f, point.y)
        val frontCenter = Point(point.x-22.5f, point.y)
        val backLeft = Point(backCenter.x, backCenter.y-15f)
        val backRight = Point(backCenter.x, backCenter.y+15f)
        val frontLeft = Point(frontCenter.x, frontCenter.y-15f)
        val frontRight = Point(frontCenter.x, frontCenter.y+15f)
        botLocationChanged(BotLocation(0.0, backLeft, backRight, frontLeft, frontRight))
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

data class Path(val point: Point, val front: Boolean)