package main.controllers.bot

import main.controllers.*
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

object BotControllerSweep : BotLocationManager.Listener, BallsManager.Listener {

    private val TAG = "BotControllerSweep"

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
            BotLocationManager.addListener(this)
            BallsManager.addListener(this)

            Executors.newCachedThreadPool().submit {
                BotLocationManager.startBotLocationRequestForMainSensor()
            }

            Executors.newCachedThreadPool().submit {
                BallsManager.startBallsRequestForMainSensor()
            }
        }
    }

    override fun ballListChanged(balls: List<BallModel>) {
        if (!controllerRunning) {
            Log.d(TAG, "Stopped ball request from sweeper controller")
            return
        }
        BallsManager.startBallsRequestForMainSensor()
    }

    override fun botLocationChanged(botLocation: BotLocation?) {
        if (!controllerRunning) {
            Log.d(TAG, "Stopped sweeper controller")
            return
        }
        if (botLocation == null) {
            BotLocationManager.startBotLocationRequestForMainSensor()
            return
        }
        val botLocationPoint = botLocation.point()
        if (pathIndex >= path.size())
            pathIndex = 0
        Log.d(TAG)
        Log.d(TAG, "========Bot Location received($botLocationPoint)=======")
        Log.d(TAG, "========Expected point: ${path.get(pathIndex).point}======")

        val pathVertex = path.get(pathIndex)

        when {
            botLocationPoint.isAt(path.get(pathIndex).point, Const.BOT_ALLOWED_DEVIATION) -> {
                Log.d("Reached at desired point")
                moveStartPoint = botLocationPoint
                pathIndex++
                if (path.get(pathIndex).point.x == -1f) {
                    Utils.sendDoorOpenToBot()
                    Thread.sleep(3000)
                    Utils.sendDoorCloseToBot()
                    pathIndex++
                }
                Utils.getPathToPoint(botLocation, pathVertex)

            } else -> {
                Log.d("Bot deviated from desired path")
                if (path.get(pathIndex).front) {
                    val point = path.get(pathIndex).point
                    moveStartPoint = botLocationPoint
                    if (Line(point, botLocationPoint).length() < 30) {
                        createReversePath(botLocation)
                    } else {
                        Utils.getPathToPoint(botLocation, pathVertex)
                    }
                } else {
                    Utils.getPathToPoint(botLocation, pathVertex)
                }
            }

        }
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
        Utils.sendPathToBot(pathList)

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
        path.vertices.add(PathVertex(Point(230f, 105f), true))
        path.vertices.add(PathVertex(Point(255f, 105f), true))
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
}
