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
        private var TAG = "BOT CONTROLLER       "
        private var instance = BotControlManager()

        fun get(): BotControlManager {
            return instance
        }
    }

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
        if (botOperatorRunning)
            return
        botOperatorRunning = true
        Executors.newCachedThreadPool().submit {
            botOperatorLoop()
        }
    }


    private fun botOperatorLoop() {
        try {
            while (true) {
                println("Status: $status")
                when (status) {
                    BotStatus.LAZY -> {
                        setBotModeWaitForBotResponse()
                        sendResetToBot()
                    }
                    BotStatus.WAIT_BOT_RESPONSE -> {
                    }
                    BotStatus.FIND -> {
                        val ball = BallsManager.get().getRankOneBall()
                        if (ball != null)
                            moveTo(ball)
                        else {
                            LogForm.logger.println(TAG, "No balls found")
                        }
                    }
                    BotStatus.COLLECT -> {
                        checkBotInPathToBallOrNot()
                    }
                    BotStatus.BOT_FULL -> {
                        moveTo(Const.POST_LOCATION, true)
                        setBotModeMovingToDump()
                    }
                    BotStatus.MOVING_TO_DUMP -> {
                        checkBotInPathToPostOrNot()
                    }
                    BotStatus.DUMPING -> {
                        sendDoorOpenToBot()
                        Thread.sleep(3000)
                        setBotModeLazy()
                    }
                }
                Thread.sleep(500)
            }
        } catch (e: Exception) {
            botOperatorRunning = false
        }
    }

    private fun moveTo(ballModel: BallModel) {
        status = BotStatus.COLLECT
        targetBall = ballModel
        moveTo(ballModel.ball.center, false)
    }

    private fun checkBotInPathToBallOrNot() {
        val botLocation = BotLocationManager.get().getBotLocation()
        if (targetBall == null || moveStartPoint == null || botLocation == null) {
            setBotModeFind()
        } else {
            if (botLocation.point().isAt(targetBall!!.ball.center, Const.BOT_WIDTH)) {
                LogForm.logger.println(TAG, "Bot reached target ball, Bot:${botLocation.point()}, Ball: ${targetBall!!.ball.center}")
                collectedBallCount++
                if (collectedBallCount >= Const.BOT_MAX_BALL_CAPACITY)
                    setBotModeReadyToDump()
                else
                    setBotModeFind()
            } else if (botLocation.point().isOnLine(Line(targetBall!!.ball.center, moveStartPoint!!), Const.BOT_WIDTH))
                if (Line(botLocation.point(), moveStartPoint!!).length() > Const.BOT_MIN_DIST_IN_UNIT_TIME)
                //TODO(Also make sure that bot moved enough distance from last point.)
                    LogForm.logger.println(TAG, "Bot not moved from ${botLocation.point()}")
                else
                    LogForm.logger.println(TAG, "Bot reached at ${botLocation.point()}")
            else
                setBotModeFind()
        }
    }

    private fun checkBotInPathToPostOrNot() {
        val botLocation = BotLocationManager.get().getBotLocation()
        if (moveStartPoint == null || botLocation == null) {
            setBotModeReadyToDump()
        } else {
            when {
                botLocation.point().isAt(Const.POST_LOCATION, Const.BOT_WIDTH) -> {
                    LogForm.logger.println(TAG, "Bot reached target post, Post: ${Const.POST_LOCATION}")
                    setBotModeDumping()
                }
                botLocation.point().isOnLine(Line(Const.POST_LOCATION, moveStartPoint!!), Const.BOT_WIDTH) -> {
                    LogForm.logger.println(TAG, "Bot reached at ${botLocation.point()}")
                }
                else -> setBotModeReadyToDump()
            }
        }
    }

    private fun setBotModeLazy() {
        LogForm.logger.println(TAG, "Bot mode changed to lazy")
        status = BotStatus.LAZY
    }

    private fun setBotModeWaitForBotResponse() {
        LogForm.logger.println(TAG, "Bot mode changed to wait for bot response")
        status = BotStatus.WAIT_BOT_RESPONSE
    }

    private fun setBotModeFind() {
        LogForm.logger.println(TAG, "Bot mode changed to find")
        status = BotStatus.FIND
    }

    private fun setBotModeReadyToDump() {
        LogForm.logger.println(TAG, "Bot mode changed to 'ready to dump'")
        status = BotStatus.BOT_FULL
    }

    private fun setBotModeMovingToDump() {
        LogForm.logger.println(TAG, "Bot mode changed to 'Moving To dump'")
        status = BotStatus.MOVING_TO_DUMP
    }

    private fun setBotModeDumping() {
        LogForm.logger.println(TAG, "Bot mode changed to 'Dumping'")
        status = BotStatus.DUMPING
    }

    private fun moveTo(point: Point, reverse: Boolean) {
        val pathList = ArrayList<PathRequestItem>()
        val botLocation = BotLocationManager.get().getBotLocation()
        if (botLocation == null) {
            LogForm.logger.println(TAG, "Bot not found!")
            if (status == BotStatus.COLLECT)
                setBotModeFind()
        } else {
            moveStartPoint = botLocation.point()
            val botToPointLine = Line(botLocation.point(), point)
            val angle = botLocation.midLine().angleBetween(botToPointLine) * Const.RAD_TO_DEGREE

            if (angle > 0 && reverse) {
                pathList.add(PathRequestItem(Const.PATH_LEFT, angle.absoluteValue.toInt()))
                pathList.add(PathRequestItem(Const.PATH_BACKWARD, botToPointLine.length().toInt()))
            } else if (angle <= 0 && reverse) {
                pathList.add(PathRequestItem(Const.PATH_RIGHT, angle.absoluteValue.toInt()))
                pathList.add(PathRequestItem(Const.PATH_BACKWARD, botToPointLine.length().toInt()))
            }else if (angle > 0) {
                pathList.add(PathRequestItem(Const.PATH_RIGHT, angle.absoluteValue.toInt()))
                pathList.add(PathRequestItem(Const.PATH_FORWARD, botToPointLine.length().toInt()))
            } else if (angle <= 0) {
                pathList.add(PathRequestItem(Const.PATH_LEFT, angle.absoluteValue.toInt()))
                pathList.add(PathRequestItem(Const.PATH_FORWARD, botToPointLine.length().toInt()))
            }
            sendPathToBot(pathList)
            //TODO(Avoid obstacle)
        }
    }

    private fun sendPathToBot(pathList: ArrayList<PathRequestItem>) {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - PATH").sendPath(pathList)
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            LogForm.logger.println(TAG, "Sent path to bot successfully")
                        }
                    }, {})
        })
    }

    private fun sendStopToBot() {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - STOP").stop()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            LogForm.logger.println(TAG, "Sent stop to bot successfully")
                        }
                    }, {})
        })
    }

    private fun sendDoorOpenToBot() {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - DOOR OPEN").doorOpen()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            LogForm.logger.println(TAG, "Sent door open to bot successfully")
                        }
                    }, {})
        })
    }

    private fun sendResetToBot() {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - RESET").reset()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            LogForm.logger.println(TAG, "Bot started")
                            status = BotStatus.FIND
                        }
                    }, { error ->
                        LogForm.logger.println(TAG, "Unable to start bot, Error: ${error.message}")
                    })
        })
    }
}

enum class BotStatus { LAZY, WAIT_BOT_RESPONSE, FIND, COLLECT, BOT_FULL, MOVING_TO_DUMP, DUMPING }