package main.controllers.bot

import main.forms.LogForm
import java.util.concurrent.Executors


class BotControlManager {
    companion object {
        private var instance = BotControlManager()

        fun get(): BotControlManager {
            return instance
        }
    }

    private var status: BotStatus

    init {
        status = BotStatus.LAZY
    }

    fun start() {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create().reset()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            LogForm.logger.println("Success --> " + result.message)
                            status = BotStatus.LAZY
                        } else {
                            LogForm.logger.println("Error --> " + result.message)
                        }
                    }, { error ->
                        LogForm.logger.println("Error --> " + error.message)
                    })
        })
    }

    private fun reStart() {
        stop()
        start()
    }

    fun stop() {
    }
}

enum class BotStatus { LAZY, FIND, COLLECT, DUMP }