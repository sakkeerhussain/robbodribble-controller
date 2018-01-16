package main.controllers.bot

import main.controllers.BallModel
import main.controllers.BallsManager
import main.controllers.BotLocationManager
import main.controllers.Const
import main.forms.LogForm
import main.geometry.Line
import main.geometry.Point
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue


class BotControlManager {
    companion object {
        private var TAG = "Bot Controller"
        private var instance = BotControlManager()

        fun get(): BotControlManager {
            return instance
        }
    }

    private var status: BotStatus
    private var targetBall: BallModel?
    private var moveStartPoint: Point?
    private var collectedBallCount: Int

    init {
        status = BotStatus.LAZY
        targetBall = null
        moveStartPoint = null
        collectedBallCount = 0
    }

    fun startBotOperator() {
        var restartOperator = true
        when (status) {
            BotStatus.LAZY -> {
                println("Lazy")
                restartOperator = false
                sendResetToBot()
            }
            BotStatus.FIND -> {
                println("Find")
                val ball = BallsManager.get().getRankOneBall()
                if (ball != null)
                    moveTo(ball)
                else {
                    LogForm.logger.println(TAG, "No balls found")
                }
            }
            BotStatus.COLLECT -> {
                println("Collect")
                checkBotInPathToBallOrNot()
            }
            BotStatus.DUMP -> {
                println("Dump")
            }
        }
        if (restartOperator) {
            Thread.sleep(500)
            startBotOperator()
        }
    }

    private fun moveTo(ballModel: BallModel) {
        status = BotStatus.COLLECT
        targetBall = ballModel
        moveTo(ballModel.ball.center)
    }

    private fun checkBotInPathToBallOrNot() {
        val botLocation = BotLocationManager.get().getBotLocation()
        if (targetBall == null || moveStartPoint == null || botLocation == null) {
            setBotModeFind()
        } else {
            if (botLocation.point().isAt(targetBall!!.ball.center, Const.BOT_WIDTH)) {
                LogForm.logger.println(TAG,"Bot reached target ball, Bot:${botLocation.point()}, Ball: ${targetBall!!.ball.center}")
                collectedBallCount++
                if (collectedBallCount >= Const.BOT_MAX_BALL_CAPACITY)
                    setBotModeDump()
                else
                    setBotModeFind()
            } else if (botLocation.point().isOnLine(Line(targetBall!!.ball.center, moveStartPoint!!), Const.BOT_WIDTH))
                LogForm.logger.println(TAG,"Bot reached at ${botLocation.point()}")
            else
                setBotModeFind()
        }
    }

    private fun setBotModeFind() {
        LogForm.logger.println(TAG,"Bot mode changed to find")
        status = BotStatus.FIND
        sendStopToBot()
    }

    private fun setBotModeDump() {
        LogForm.logger.println(TAG,"Bot mode changed to dump")
        status = BotStatus.DUMP
    }

    private fun moveTo(point: Point) {
        val pathList = ArrayList<PathRequestItem>()
        val botLocation = BotLocationManager.get().getBotLocation()
        if (botLocation == null) {
            LogForm.logger.println(TAG,"Bot not found!")
            setBotModeFind()
        } else {
            moveStartPoint = botLocation.point()
            val botToPointLine = Line(botLocation.frontSide().mid(), point)
            val angle = botLocation.midLine().angleBetween(botToPointLine) * Const.RAD_TO_DEGREE
            if (angle > 0) {
                pathList.add(PathRequestItem(Const.PATH_LEFT, angle.absoluteValue.toInt()))
            } else {
                pathList.add(PathRequestItem(Const.PATH_RIGHT, angle.absoluteValue.toInt()))
            }
            pathList.add(PathRequestItem(Const.PATH_FORWARD, botToPointLine.length().toInt()))
            sendPathToBot(pathList)
            //TODO(Avoid obstacle)
        }
    }

    private fun sendPathToBot(pathList: ArrayList<PathRequestItem>) {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - Path").sendPath(pathList)
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            LogForm.logger.println(TAG,"Sent path to bot successfully")
                        }
                    }, {})
        })
    }

    private fun sendStopToBot() {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - Stop").stop()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            LogForm.logger.println(TAG,"Sent stop to bot successfully")
                        }
                    }, {})
        })
    }

    fun sendResetToBot() {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - Reset").reset()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            LogForm.logger.println(TAG,"Bot started")
                            status = BotStatus.FIND
                            startBotOperator()
                        }
                    }, { error ->
                        LogForm.logger.println(TAG,"Unable to start bot")
                    })
        })
    }
}

enum class BotStatus { LAZY, FIND, COLLECT, DUMP }