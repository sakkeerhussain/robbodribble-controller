package main.controllers.bot

import com.google.gson.Gson
import main.controllers.BotLocation
import main.controllers.BotLocationManager
import main.controllers.Const
import main.geometry.Line
import main.utils.Log
import main.utils.PathVertex
import java.util.concurrent.Executors
import kotlin.math.absoluteValue

object Utils {
    private const val TAG = "Bot-Utils"

    fun getPathToPoint(botLocation: BotLocation, path: PathVertex): ArrayList<PathRequestItem> {
        val botToPointLine = Line(botLocation.point(), path.point)
        Log.d(TAG, "Finding path from ${botLocation.point()} to ${path.point}")
        Log.d(TAG, "Bot line angle: ${botLocation.midLine().angleInDegree()}")
        Log.d(TAG, "Bot to target line angle: ${botToPointLine.angleInDegree()}")
        var angle = botLocation.midLine().angleBetween(botToPointLine)
        Log.d(TAG, "Angle between bot line and target: $angle")
        val pathList = ArrayList<PathRequestItem>()
        if (path.front) {
            var distance = botToPointLine.length().toInt()
            //Reducing bot distance to 50 intentionally for increasing accuracy
            if (distance > 80) {
                distance = 50
            }

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
        return pathList
    }

    fun getReversePathToAdjustForwardMotion(botLocation: BotLocation, pathVertex: PathVertex): ArrayList<PathRequestItem> {

        val botToPointLine = Line(botLocation.point(), pathVertex.point)
        val angle = botLocation.midLine().angleBetween(botToPointLine)
        val pathList = ArrayList<PathRequestItem>()
        if (pathVertex.front) {
            when {
                angle < 0 ->
                    pathList.add(PathRequestItem(Const.PATH_LEFT, 10))
                angle > 0 ->
                    pathList.add(PathRequestItem(Const.PATH_RIGHT, 10))
            }
            pathList.add(PathRequestItem(Const.PATH_BACKWARD, 30))
        }
        return pathList
    }

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

    fun sendPathToBot(pathList: ArrayList<PathRequestItem>, listener: Listener) {
        Log.d(TAG, "Sending path to point. Data: ${Gson().toJson(pathList)}")
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - PATH").sendPath(pathList)
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            Log.d(TAG, "Sent path to bot successfully")
                            listener.botRespondedSuccess()
                        }else {
                            listener.botRespondedFailure()
                        }
                    }, { error ->
                        Log.d(TAG, "Sent path to bot failed, message:${error.localizedMessage}")
                        sendResetToBot(object: Listener() {
                            override fun botResponded() {
                                listener.botRespondedFailure()
                            }
                        })
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

    fun sendResetToBot(listener: Listener) {
        Executors.newCachedThreadPool().submit({
            BotCommunicationService.Factory.create("BOT CONTROL - RESET").reset()
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            Log.d(TAG, "Bot started")
                            listener.botRespondedSuccess()
                        } else {
                            listener.botRespondedFailure()
                        }
                    }, { error ->
                        Log.d(TAG, "Unable to start bot, Error: ${error.message}")
                        listener.botRespondedFailure()
                    })
        })
    }

    abstract class Listener {
        open fun botRespondedSuccess() {
            botResponded()
        }
        open fun botRespondedFailure() {
            botResponded()
        }
        abstract fun botResponded()
    }
}