package main.controllers.bot

import com.google.gson.Gson
import main.controllers.BotLocation
import main.controllers.BotLocationManager
import main.controllers.Const
import main.controllers.PathManager
import main.forms.BallsUI
import main.geometry.Line
import main.geometry.Point
import main.utils.Log
import main.utils.Path
import main.utils.PathVertex
import java.util.concurrent.Executors
import kotlin.math.absoluteValue

//fun main(args: Array<String>) {
//    Log.d("Initialising...")
//    when {
//        args[0] == "post-1" -> BotControllerSweep().start(1)
//        args[0] == "post-2" -> BotControllerSweep().start(2)
//        else -> Log.d("Post not specified")
//    }
//}

class BotControllerSweep private constructor() : BotLocationManager.Listener {
    private val TAG = "BotControllerSweep"

    companion object {
        private var instance: BotControllerSweep? = null

        fun get(): BotControllerSweep {
            if (instance == null)
                instance = BotControllerSweep()
            return instance!!
        }
    }

    val path = Path()
    private var pathIndex = 0
    private var moveStartPoint: Point? = null
    private var controllerRunning = false

    fun stop() {
        Log.d(TAG, "Stopping sweep controller...")
        controllerRunning = false
    }

    fun start() {
        if (controllerRunning) {
            Log.d(TAG, "Sweep controller is already running...")
            return
        }
        Executors.newCachedThreadPool().submit {
            Log.d(TAG, "Starting sweeper controller...")
            controllerRunning = true
            init()
            BotLocationManager.get().addListener(this)
            BotLocationManager.get().startBotLocationRequestForMainSensor()
        }
    }

    override fun botLocationChanged(botLocation: BotLocation?) {
        if (!controllerRunning) {
            Log.d(TAG, "Stopped sweeper controller")
            return
        }
        if (botLocation == null) {
            BotLocationManager.get().startBotLocationRequestForMainSensor()
            return
        }
        val botLocationPoint = botLocation.point()
        if (pathIndex >= path.size())
            pathIndex = 0
        Log.d(TAG)
        Log.d(TAG, "========Bot Location received($botLocationPoint)=======")
        Log.d(TAG, "========Expected point: ${path.get(pathIndex).point}======")

        when {
            botLocationPoint.isAt(path.get(pathIndex).point, Const.BOT_ALLOWED_DEVIATION) -> {
                Log.d("Reached at desired point")
                moveStartPoint = botLocationPoint
                pathIndex++
                if (path.get(pathIndex).point.x == -1f) {
                    sendDoorOpenToBot()
                    Thread.sleep(5000)
                    sendDoorCloseToBot()
                    pathIndex++
                }
                createPathToPoint(botLocation)

            }
            botLocationPoint.isOnLine(Line(path.get(pathIndex).point, moveStartPoint!!), Const.BOT_ALLOWED_DEVIATION) -> {
                Log.d("Bot is on line to target")

//                if (Line(botLocationPoint, moveStartPoint!!).length() < Const.BOT_MIN_DIST_IN_UNIT_TIME) {
//                    Log.d("Bot not moving")
//                    sendStopToBot()
//                    createPathToPoint(botLocation)
//                    moveStartPoint = botLocationPoint
//                } else {
//                    Log.d("Bot reached at $botLocationPoint")
//                    moveStartPoint = botLocationPoint
//                    BotLocationManager.get().startBotLocationRequestForMainSensor()
//                }
                createPathToPoint(botLocation)
                moveStartPoint = botLocationPoint
            }
            else -> {
                Log.d("Bot deviated from desired path")
                if (path.get(pathIndex).front) {
                    val point = path.get(pathIndex).point
                    moveStartPoint = botLocationPoint
                    if (Line(point, botLocationPoint).length() < 30) {
                        createReversePath(botLocation)
                    } else {
                        createPathToPoint(botLocation)
                    }
                } else {
                    createPathToPoint(botLocation)
                }
            }

        }
    }

    private fun createPathToPoint(botLocation: BotLocation) {
        val path = path.get(pathIndex)
        val botToPointLine = Line(botLocation.point(), path.point)
        Log.d(TAG, "Finding path from ${botLocation.point()} to ${path.point}")
        Log.d(TAG, "Bot line angle: ${botLocation.midLine().angleInDegree()}")
        Log.d(TAG, "Bot to target line angle: ${botToPointLine.angleInDegree()}")
        var angle = botLocation.midLine().angleBetween(botToPointLine)
        Log.d(TAG, "Angle between bot line and target: $angle")
        //val ballInFront = Line(botLocation.frontSide().mid(), path.point).length() < Line(botLocation.backSide().mid(), path.point).length()
//        if (ballInFront)
//            Log.d(TAG, "Target is in-front of bot")
//        else
//            Log.d(TAG, "Target is behind bot")
        val pathList = ArrayList<PathRequestItem>()
        if (path.front) {
            val distance = botToPointLine.length().toInt()
            //Left move correction
            angle += (distance * 0.133333333)

            if (angle > 180) {
                angle -= 360
            } else if (angle < -180) {
                angle += 360
            }

            when {
                angle < 0 ->
                    pathList.add(PathRequestItem(Const.PATH_LEFT, angle.absoluteValue.toInt()))
                angle > 0 ->
                    pathList.add(PathRequestItem(Const.PATH_RIGHT, angle.absoluteValue.toInt()))
            }
            pathList.add(PathRequestItem(Const.PATH_FORWARD, distance))
        } else {
            if (angle > 0)
                pathList.add(PathRequestItem(Const.PATH_LEFT, 180 - angle.absoluteValue.toInt()))
            else if (angle < 0)
                pathList.add(PathRequestItem(Const.PATH_RIGHT, 180 - angle.absoluteValue.toInt()))
            pathList.add(PathRequestItem(Const.PATH_BACKWARD, botToPointLine.length().toInt()))
        }
        sendPathToBot(pathList)
//        Log.d(TAG, "PathVertex list: $pathList")
//        callBotReachedCallBackForTesting(path.point)
    }

    private fun createReversePath(botLocation: BotLocation) {
        val path = path.get(pathIndex)
        val botToPointLine = Line(botLocation.point(), path.point)
        val angle = botLocation.midLine().angleBetween(botToPointLine)
        val pathList = ArrayList<PathRequestItem>()
        if (path.front) {
            when {
                angle < 0 ->
                    pathList.add(PathRequestItem(Const.PATH_LEFT, 10))
                angle > 0 ->
                    pathList.add(PathRequestItem(Const.PATH_RIGHT, 10))
            }
            pathList.add(PathRequestItem(Const.PATH_BACKWARD, 30))
        }
        sendPathToBot(pathList)

//        Log.d(TAG, "PathVertex list: $pathList")
//        callBotReachedCallBackForTesting(path.point)
    }

    fun init() {
        //Center to post
        path.vertices.add(PathVertex(Point(45f, 140f), true))
        path.vertices.add(PathVertex(Point(85f, 140f), true))
        path.vertices.add(PathVertex(Point(125f, 130f), true))
        path.vertices.add(PathVertex(Point(140f, 90f), true))
        path.vertices.add(PathVertex(Point(155f, 50f), true))
        path.vertices.add(PathVertex(Point(105f, 50f), true))
        path.vertices.add(PathVertex(Point(60f, 50f), true))
        path.vertices.add(PathVertex(Point(20f, 50f), true))
        path.vertices.add(PathVertex(Point(10f, 95f), true))
        path.vertices.add(PathVertex(Point(40f, 90f), true))
        path.vertices.add(PathVertex(Point(4f, 90f), false))
        path.vertices.add(PathVertex(Point(-1f, -1f), true)) //Open Door

        //Sweep 1
        path.vertices.add(PathVertex(Point(30f, 105f), true))
        path.vertices.add(PathVertex(Point(80f, 105f), true))
        path.vertices.add(PathVertex(Point(130f, 105f), true))
        path.vertices.add(PathVertex(Point(180f, 105f), true))
        path.vertices.add(PathVertex(Point(230f, 135f), true))
        path.vertices.add(PathVertex(Point(255f, 135f), true))
        path.vertices.add(PathVertex(Point(200f, 135f), true))
        path.vertices.add(PathVertex(Point(150f, 135f), true))
        path.vertices.add(PathVertex(Point(100f, 135f), true))
        path.vertices.add(PathVertex(Point(60f, 135f), true))
        path.vertices.add(PathVertex(Point(30f, 135f), true))
        path.vertices.add(PathVertex(Point(10f, 85f), true))
        path.vertices.add(PathVertex(Point(40f, 90f), true))
        path.vertices.add(PathVertex(Point(4f, 90f), false))
        path.vertices.add(PathVertex(Point(-1f, -1f), true)) //Open Door

        //Sweep 2
        path.vertices.add(PathVertex(Point(30f, 75f), true))
        path.vertices.add(PathVertex(Point(80f, 75f), true))
        path.vertices.add(PathVertex(Point(130f, 75f), true))
        path.vertices.add(PathVertex(Point(180f, 75f), true))
        path.vertices.add(PathVertex(Point(220f, 75f), true))
        path.vertices.add(PathVertex(Point(250f, 75f), true))
        path.vertices.add(PathVertex(Point(250f, 45f), true))
        path.vertices.add(PathVertex(Point(220f, 45f), true))
        path.vertices.add(PathVertex(Point(180f, 45f), true))
        path.vertices.add(PathVertex(Point(130f, 45f), true))
        path.vertices.add(PathVertex(Point(80f, 45f), true))
        path.vertices.add(PathVertex(Point(30f, 45f), true))
        path.vertices.add(PathVertex(Point(10f, 95f), true))
        path.vertices.add(PathVertex(Point(40f, 90f), true))
        path.vertices.add(PathVertex(Point(4f, 90f), false))
        path.vertices.add(PathVertex(Point(-1f, -1f), true)) //Open Door
        Log.d(TAG, "Starting...")
        moveStartPoint = path.get(pathIndex).point

        PathManager.updatePath(path)
    }

    private fun callBotReachedCallBackForTesting(point: Point) {
        val backCenter = Point(point.x + 7.5f, point.y)
        val frontCenter = Point(point.x - 22.5f, point.y)
        val backLeft = Point(backCenter.x, backCenter.y - 15f)
        val backRight = Point(backCenter.x, backCenter.y + 15f)
        val frontLeft = Point(frontCenter.x, frontCenter.y - 15f)
        val frontRight = Point(frontCenter.x, frontCenter.y + 15f)
        botLocationChanged(BotLocation(0.0, backLeft, backRight, frontLeft, frontRight))
    }

    private fun sendDoorOpenToBot() {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - DOOR OPEN").doorOpen()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            Log.d(TAG, "Sent door open to bot successfully")
                        }
                    }, { error ->
                        Log.d(TAG, "Sent door open to bot failed, message:${error.localizedMessage}")
                    })
        })
    }

    private fun sendDoorCloseToBot() {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - DOOR CLOSE").doorClose()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            Log.d(TAG, "Sent door close to bot successfully")
                        }
                    }, { error ->
                        Log.d(TAG, "Sent door close to bot failed, message:${error.localizedMessage}")
                    })
        })
    }

    private fun sendPathToBot(pathList: ArrayList<PathRequestItem>) {
        Log.d(TAG, "Sending path to point. Data: ${Gson().toJson(pathList)}")
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - PATH").sendPath(pathList)
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            Log.d(TAG, "Sent path to bot successfully")
                        }
                        BotLocationManager.get().startBotLocationRequestForMainSensor()
                    }, { error ->
                        Log.d(TAG, "Sent path to bot failed, message:${error.localizedMessage}")
                        BotLocationManager.get().startBotLocationRequestForMainSensor()
                    })
        })
    }

    private fun sendStopToBot() {
        Log.d(TAG, "Sending stop to bot...")
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - STOP").stop()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            Log.d(TAG, "Sent stop to bot successfully")
                        }
                    }, { error ->
                        Log.d(TAG, "Sent stop to bot failed, message:${error.localizedMessage}")
                    })
        })
    }

}
