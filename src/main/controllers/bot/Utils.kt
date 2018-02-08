package main.controllers.bot

import com.google.gson.Gson
import main.controllers.BotLocationManager
import main.utils.Log
import java.util.concurrent.Executors

object Utils {
    private const val TAG = "Bot-Utils"

    fun sendDoorOpenToBot() {
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

    fun sendDoorCloseToBot() {
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

    fun sendPathToBot(pathList: ArrayList<PathRequestItem>) {
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

    fun sendStopToBot() {
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

    fun sendResetToBot() {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - RESET").reset()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            Log.d(TAG, "Bot started")
                        }
                    }, { error ->
                        Log.d(TAG, "Unable to start bot, Error: ${error.message}")
                    })
        })
    }
}