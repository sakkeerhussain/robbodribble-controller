package main.controllers.bot

import com.google.gson.GsonBuilder
import main.controllers.*
import main.geometry.Line
import main.geometry.Point
import main.opencv.OpenCV
import main.utils.Log
import main.utils.PathVertex
import java.io.File
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue


fun main(args: Array<String>) {
    OpenCV.init()
    BotControlManager.startBotOperator()
}

object BotControlManager : BotLocationManager.Listener, BallsManager.Listener {
    private var TAG = "BOT CONTROLLER       "

    private var status: BotStatus
    private var targetBall: BallModel?
    private var moveStartPoint: Point?
    private var collectedBallCount: Int
    private var botOperatorRunning: Boolean
    private var mBotStatusAndConfig = BotStatusAndConfig()

    init {
        loadBotConfigFromFile()
        status = BotStatus.FIND
        targetBall = null
        moveStartPoint = null
        collectedBallCount = 0
        botOperatorRunning = false
    }

    fun startBotOperator() {
        if (botOperatorRunning) {
            Log.d(TAG, "Bot operator already running.")
            return
        }
        botOperatorRunning = true
        BotLocationManager.addListener(this)
        BallsManager.addListener(this)

        Executors.newCachedThreadPool().submit {
            BallsManager.startBallsRequestForMainSensor()
        }
        Executors.newCachedThreadPool().submit {
            if (mBotStatusAndConfig.alreadyRunning) {
                BotLocationManager.startBotLocationRequestForMainSensor()
            } else {
                val pathList = getFirstMovementPath()
                Utils.sendPathToBot(pathList, object : Utils.Listener() {
                    override fun botResponded() {
                        BotLocationManager.startBotLocationRequestForMainSensor()
                    }
                })
                mBotStatusAndConfig.alreadyRunning = true
                saveBotConfigToFile()
            }
        }
    }

    fun stopBotOperator() {
        if (botOperatorRunning) {
            botOperatorRunning = false
            Log.d(TAG, "Bot operator stop request placed")
            return
        }
        Log.d(TAG, "Bot operator stopped already")
    }

    override fun botLocationChanged(botLocation: BotLocation?) {
        if (!botOperatorRunning) {
            Log.d(TAG, "Bot operator stopped")
            return
        }

        if (botLocation == null) {
            BotLocationManager.startBotLocationRequestForMainSensor()
        } else {
            botOperatorLoop(botLocation)
        }
    }

    private fun botOperatorLoop(botLocation: BotLocation) {
        try {
            Log.d("Status: $status")
            when (status) {
                BotStatus.FIND -> {
                    processFindMode(botLocation)
                }
                BotStatus.COLLECT -> {
                    checkBotInPathToBallOrNot(botLocation)
                }
                BotStatus.MOVING_TO_DUMP -> {
                    checkBotInPathToPostOrNot(botLocation)
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, e.localizedMessage)
        }
    }

    private fun processBotModeDumping(botLocation: BotLocation) {
        Utils.sendDoorOpenToBot()
        Thread.sleep(3000)
        processFindMode(botLocation)
    }

    private fun processBotModeFull(botLocation: BotLocation) {
        //TODO - Move with specific post approach path
        setBotModeMovingToDump()
        moveTo(Const.POST_LOCATION, false, botLocation)
    }

    private fun processFindMode(botLocation: BotLocation) {
        val ball = BallsManager.getRankOneBall()
        if (ball != null) {
            setBotModeFind()
            moveTo(ball, botLocation)
        } else {
            Log.d(TAG, "No balls found")
            Thread.sleep(500)
            processFindMode(botLocation)
        }
    }

    private fun moveTo(ballModel: BallModel, botLocation: BotLocation) {
        targetBall = ballModel
        moveTo(ballModel.ball.center, true, botLocation)
    }

    private fun checkBotInPathToBallOrNot(botLocation: BotLocation) {
        if (targetBall == null || moveStartPoint == null) {
            processFindMode(botLocation)
        } else {
            if (botLocation.point().isAt(targetBall!!.ball.center, Const.BOT_ALLOWED_DEVIATION_FOR_BALLS)) {
                Log.d(TAG, "Bot reached target ball, " +
                        "Bot:${botLocation.point()}, " +
                        "Ball: ${targetBall!!.ball.center}")
                collectedBallCount++
                if (collectedBallCount >= Const.BOT_MAX_BALL_CAPACITY) {
                    processBotModeFull(botLocation)
                    return
                }
            }
            processFindMode(botLocation)
        }
    }

    private fun checkBotInPathToPostOrNot(botLocation: BotLocation) {
        if (moveStartPoint == null) {
            processBotModeFull(botLocation)
        } else {
            when {
                botLocation.point().isAt(Const.POST_LOCATION, Const.BOT_WIDTH) -> {
                    Log.d(TAG, "Bot reached target post, Post: ${Const.POST_LOCATION}")
                    processBotModeDumping(botLocation)
                }
                else -> processBotModeFull(botLocation)
            }
        }
    }

    private fun setBotModeFind() {
        Log.d(TAG, "Bot mode changed to find")
        status = BotStatus.FIND
    }

    private fun setBotModeMovingToDump() {
        Log.d(TAG, "Bot mode changed to 'Moving To dump'")
        status = BotStatus.MOVING_TO_DUMP
    }

//    private fun moveTo(pathVertices: List<PathVertex>, reverse: Boolean, botLocation: BotLocation) {
//        for (pathVertex in pathVertices) {
//            move
//        }
//    }

    private fun moveTo(point: Point, front: Boolean, botLocation: BotLocation) {
        moveStartPoint = botLocation.point()
        val pathList = Utils.getPathToPoint(botLocation, PathVertex(point, front))
        Utils.sendPathToBot(pathList, object : Utils.Listener() {
            override fun botResponded() {
                BotLocationManager.startBotLocationRequestForMainSensor()
            }
        })
        //TODO(Avoid obstacle)
    }

    private fun loadBotConfigFromFile() {
        try {
            val file = File(Const.FileName.BOT_STATUS)
            if (file.exists()) {
                Log.d(TAG, "Loading bot running status from ${file.name}")
                val boardReferenceJson = file.readText()
                val gson = GsonBuilder().create()
                mBotStatusAndConfig = gson.fromJson(boardReferenceJson, BotStatusAndConfig::class.java)
                return
            }
        } catch (e: Exception) {
        }
        mBotStatusAndConfig = BotStatusAndConfig()
    }

    private fun saveBotConfigToFile() {
        try {
            val file = File(Const.FileName.BOT_STATUS)
            if (!file.exists()) {
                File(file.parent).mkdir()
            }
            val botStatusAndConfigJson = GsonBuilder().create().toJson(mBotStatusAndConfig)
            file.writeText(botStatusAndConfigJson)
        } catch (e: Exception) {
        }
    }

    private fun getFirstMovementPath(): ArrayList<PathRequestItem> {
        val pathList = ArrayList<PathRequestItem>()
        pathList.add(PathRequestItem(Const.PATH_FORWARD, 50))
        pathList.add(PathRequestItem(Const.PATH_RIGHT, 10))
        pathList.add(PathRequestItem(Const.PATH_FORWARD, 50))
        pathList.add(PathRequestItem(Const.PATH_RIGHT, 10))
        pathList.add(PathRequestItem(Const.PATH_FORWARD, 80))
        pathList.add(PathRequestItem(Const.PATH_RIGHT, 170))
        return pathList
    }

    override fun ballListChanged(balls: List<BallModel>) {
        if (!botOperatorRunning) {
            Log.d(TAG, "Ball request from bot operator stopped")
            return
        }
        BallsManager.startBallsRequestForMainSensor()
    }
}

enum class BotStatus { FIND, COLLECT, MOVING_TO_DUMP }