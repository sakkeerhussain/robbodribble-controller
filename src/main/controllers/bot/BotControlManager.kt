package main.controllers.bot

import main.controllers.BallModel
import main.controllers.BallsManager
import main.forms.LogForm
import main.geometry.Point
import java.util.*
import java.util.concurrent.Executors
import kotlin.concurrent.timerTask


class BotControlManager {
    companion object {
        private var instance = BotControlManager()

        fun get(): BotControlManager {
            return instance
        }
    }

    private var status: BotStatus
    private var targetBall: BallModel?

    init {
        status = BotStatus.LAZY
        targetBall = null
    }

    fun start() {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create().reset()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            LogForm.logger.println("Bot started")
                            status = BotStatus.FIND
                            awakeBotOperator()
                        }
                    }, {})
        })
    }

    fun reStart() {
        stop()
        start()
    }

    fun stop() {
    }

    private fun awakeBotOperator() {
        when (status) {
            BotStatus.LAZY -> {
                start()
            }
            BotStatus.FIND -> {
                val ball = BallsManager.get().getRankOneBall()
                if (ball != null)
                    moveTo(ball)
                else {
                    LogForm.logger.println("No balls found")
                    wait(1)
                }
            }
            BotStatus.COLLECT -> {

            }
            BotStatus.DUMP -> {

            }
        }
    }

    private fun wait(time: Int) {
        LogForm.logger.println("Waiting for $time seconds")
        Timer().schedule(timerTask{ awakeBotOperator() }, (time*1000).toLong())
    }

    private fun moveTo(ballModel: BallModel) {
        status = BotStatus.COLLECT
        moveTo(ballModel.ball.center)
        //TODO(Replace 2 seconds by time 10%% of total time to reach the ball)
        wait(2)
    }

    private fun moveTo(point: Point) {

        //TODO(Turn bot to the particular angle)
        //TODO(Drive to the ball)
        //TODO(Avoid obstacle)
    }

    fun sendPathToBot() {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create().sendPath()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            LogForm.logger.println("Sent path to bot successfully")
                            status = BotStatus.FIND
                            awakeBotOperator()
                        }
                    }, {})
        })
    }
}

enum class BotStatus { LAZY, FIND, COLLECT, DUMP }