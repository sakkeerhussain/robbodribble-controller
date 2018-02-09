package main.controllers.bot

import main.controllers.*
import main.geometry.Line
import main.geometry.Point
import main.utils.Log
import main.utils.PathVertex
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue


object BotControlManager : BotLocationManager.Listener {
    private var TAG = "BOT CONTROLLER       "

    private var status: BotStatus
    private var targetBall: BallModel?
    private var moveStartPoint: Point?
    private var collectedBallCount: Int
    private var botOperatorRunning: Boolean

    init {
        status = BotStatus.LAZY
        targetBall = null
        moveStartPoint = null
        collectedBallCount = 0
        botOperatorRunning = false
    }

    fun startBotOperator() {
        setBotModeLazy()
        if (botOperatorRunning) {
            Log.d(TAG, "Bot operator already running.")
            return
        }
        botOperatorRunning = true
        Executors.newCachedThreadPool().submit {
            BotLocationManager.addListener(this)
            BotLocationManager.startBotLocationRequestForMainSensor()
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

        if (botLocation == null){
            BotLocationManager.startBotLocationRequestForMainSensor()
        }else {
            botOperatorLoop(botLocation)
        }
    }

    private fun botOperatorLoop(botLocation: BotLocation) {
        try {
            Log.d("Status: $status")
            when (status) {
                BotStatus.LAZY -> {
                    setBotModeWaitForBotResponse()
                    Utils.sendResetToBot()
                    setBotModeFind()
                }
                BotStatus.WAIT_BOT_RESPONSE -> {
                }
                BotStatus.FIND -> {
                    val ball = BallsManager.getRankOneBall()
                    if (ball != null)
                        moveTo(ball, botLocation)
                    else {
                        Log.d(TAG, "No balls found")
                    }
                }
                BotStatus.COLLECT -> {
                    checkBotInPathToBallOrNot(botLocation)
                }
                BotStatus.BOT_FULL -> {
                    //TODO - Move with specific post approach path
                    moveTo(Const.POST_1_PATH, true, botLocation)
                    setBotModeMovingToDump()
                }
                BotStatus.MOVING_TO_DUMP -> {
                    checkBotInPathToPostOrNot(botLocation)
                }
                BotStatus.DUMPING -> {
                    Utils.sendDoorOpenToBot()
                    Thread.sleep(3000)
                    setBotModeLazy()
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, e.localizedMessage)
        }
        BotLocationManager.startBotLocationRequestForMainSensor()
    }

    private fun moveTo(ballModel: BallModel, botLocation:BotLocation) {
        status = BotStatus.COLLECT
        targetBall = ballModel
        moveTo(ballModel.ball.center, false, botLocation)
    }

    private fun checkBotInPathToBallOrNot(botLocation: BotLocation) {
        if (targetBall == null || moveStartPoint == null) {
            setBotModeFind()
        } else {
            if (botLocation.point().isAt(targetBall!!.ball.center, Const.BOT_WIDTH)) {
                Log.d(TAG, "Bot reached target ball, " +
                        "Bot:${botLocation.point()}, " +
                        "Ball: ${targetBall!!.ball.center}")
                collectedBallCount++
                if (collectedBallCount >= Const.BOT_MAX_BALL_CAPACITY)
                    setBotModeReadyToDump()
                else
                    setBotModeFind()
            } else if (botLocation.point().isOnLine(Line(targetBall!!.ball.center, moveStartPoint!!), Const.BOT_ALLOWED_DEVIATION))
                if (Line(botLocation.point(), moveStartPoint!!).length() < Const.BOT_MIN_DIST_IN_UNIT_TIME) {
                    //TODO(Also make sure that bot moved enough distance from last point.)
                    Log.d(TAG, "Bot not moved from ${moveStartPoint}")
                    setBotModeFind()
                } else {
                    Log.d(TAG, "Bot reached at ${botLocation.point()}")
                    moveStartPoint = botLocation.point()
                }
            else
                setBotModeFind()
        }
    }

    private fun checkBotInPathToPostOrNot(botLocation: BotLocation) {
        if (moveStartPoint == null) {
            setBotModeReadyToDump()
        } else {
            when {
                botLocation.point().isAt(Const.POST_LOCATION, Const.BOT_WIDTH) -> {
                    Log.d(TAG, "Bot reached target post, Post: ${Const.POST_LOCATION}")
                    setBotModeDumping()
                }
                botLocation.point().isOnLine(Line(Const.POST_LOCATION, moveStartPoint!!), Const.BOT_ALLOWED_DEVIATION) -> {
                    if (Line(botLocation.point(), moveStartPoint!!).length() < Const.BOT_MIN_DIST_IN_UNIT_TIME) {
                        //TODO(Also make sure that bot moved enough distance from last point.)
                        Log.d(TAG, "Bot not moved from ${moveStartPoint!!}")
                        setBotModeReadyToDump()
                    } else {
                        Log.d(TAG, "Bot reached at ${botLocation.point()}")
                        moveStartPoint = botLocation.point()
                    }
                }
                else -> setBotModeReadyToDump()
            }
        }
    }

    private fun setBotModeLazy() {
        Log.d(TAG, "Bot mode changed to lazy")
        status = BotStatus.LAZY
    }

    private fun setBotModeWaitForBotResponse() {
        Log.d(TAG, "Bot mode changed to wait for bot response")
        status = BotStatus.WAIT_BOT_RESPONSE
    }

    private fun setBotModeFind() {
        Log.d(TAG, "Bot mode changed to find")
        status = BotStatus.FIND
    }

    private fun setBotModeReadyToDump() {
        Log.d(TAG, "Bot mode changed to 'ready to dump'")
        status = BotStatus.BOT_FULL
    }

    private fun setBotModeMovingToDump() {
        Log.d(TAG, "Bot mode changed to 'Moving To dump'")
        status = BotStatus.MOVING_TO_DUMP
    }

    private fun setBotModeDumping() {
        Log.d(TAG, "Bot mode changed to 'Dumping'")
        status = BotStatus.DUMPING
    }

    private fun moveTo(point: List<PathVertex>, reverse: Boolean, botLocation: BotLocation) {
    }

    private fun moveTo(point: Point, reverse: Boolean, botLocation: BotLocation) {
        val pathList = ArrayList<PathRequestItem>()
        moveStartPoint = botLocation.point()
        val botToPointLine = Line(botLocation.point(), point)
        var angle = botLocation.midLine().angleBetween(botToPointLine)

        if (Line(botLocation.backSide().mid(), point).length() < Line(botLocation.point(), point).length())
            angle = 180 - angle

        if (reverse) {
            if (angle > 0) {
                pathList.add(PathRequestItem(Const.PATH_RIGHT, angle.absoluteValue.toInt()))
            } else if (angle < 0) {
                pathList.add(PathRequestItem(Const.PATH_LEFT, angle.absoluteValue.toInt()))
            }
            pathList.add(PathRequestItem(Const.PATH_BACKWARD, botToPointLine.length().toInt()))
        } else {
            if (angle > 0) {
                pathList.add(PathRequestItem(Const.PATH_LEFT, angle.absoluteValue.toInt()))
            } else if (angle < 0) {
                pathList.add(PathRequestItem(Const.PATH_RIGHT, angle.absoluteValue.toInt()))
            }
            pathList.add(PathRequestItem(Const.PATH_FORWARD, botToPointLine.length().toInt()))
        }
        Utils.sendPathToBot(pathList)
        //TODO(Avoid obstacle)
    }
}

enum class BotStatus { LAZY, WAIT_BOT_RESPONSE, FIND, COLLECT, BOT_FULL, MOVING_TO_DUMP, DUMPING }