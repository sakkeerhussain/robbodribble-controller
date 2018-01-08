package main.sensor

import main.controllers.Ball
import main.controllers.BotLocation
import java.util.concurrent.Executors
import javax.swing.JLabel
import javax.swing.JTextField

class Http {
    companion object {
        fun calibrateRef(ip: String, point: Int, lbMessage: JLabel, runnable: Runnable) {
            Executors.newCachedThreadPool().submit({
                lbMessage.text = "Loading..."
                ApiService.Factory.create(ip).calibrateRef(point)
                        .subscribe({ result ->
                            if (result.status.equals("ok")) {
                                lbMessage.text = "Success: ${result.message}"
                                runnable.run()
                            } else {
                                lbMessage.text = "Failed: ${result.message}"
                            }
                        }, { error ->
                            lbMessage.text = "Failed: ${error.message}"
                        })
            })
        }

        fun getReferencePoint(ip: String, point: Int, lbMessage: JLabel, tfX: JTextField, tfY: JTextField) {
            Executors.newCachedThreadPool().submit({
                lbMessage.text = "Loading..."
                ApiService.Factory.create(ip).getReferencePoint(point)
                        .subscribe({ result ->
                            if (result.status.equals("ok")) {
                                lbMessage.text = "Success: ${result.message}"
                                tfX.text = result.data.pointImage.x.toString()
                                tfY.text = result.data.pointImage.y.toString()
                            } else {
                                lbMessage.text = "Failed: ${result.message}"
                                tfX.text = ""
                                tfY.text = ""
                            }
                        }, { error ->
                            lbMessage.text = "Failed: ${error.message}"
                            tfX.text = ""
                            tfY.text = ""
                        })
            })
        }

        fun setReferencePoint(ip: String, point: Int, lbMessage: JLabel, xImage: Float, yImage: Float,
                              xBord: Float, yBord: Float, runnable: Runnable) {
            Executors.newCachedThreadPool().submit({
                lbMessage.text = "Loading..."
                ApiService.Factory.create(ip).setReferencePoint(point, xImage, yImage, xBord, yBord)
                        .subscribe({ result ->
                            if (result.status.equals("ok")) {
                                lbMessage.text = "Success: ${result.message}"
                                runnable.run()
                            } else {
                                lbMessage.text = "Failed: ${result.message}"
                            }
                        }, { error ->
                            lbMessage.text = "Failed: ${error.message}"
                        })
            })
        }

        fun getBalls(ip: String, lbMessage: JLabel?, listener: BallsListListener) {
            Executors.newCachedThreadPool().submit({
                lbMessage?.text = "Loading balls..."
                ApiService.Factory.create(ip).getBalls()
                        .subscribe({ result ->
                            if (result.status.equals("ok")) {
                                lbMessage?.text = "Success: ${result.message}"
                                listener.ballsListReceived(ip, result.data)
                            } else {
                                listener.ballsListReceived(ip, null)
                                lbMessage?.text = "Failed: ${result.message}"
                            }
                        }, { error ->
                            listener.ballsListFailed(ip)
                            lbMessage?.text = "Failed: ${error.message}"
                        })
            })
        }

        fun getBotLocation(ip: String, lbMessage: JLabel?, listener: BotLocationListener) {
            Executors.newCachedThreadPool().submit({
                lbMessage?.text = "Loading bot location..."
                ApiService.Factory.create(ip).getBotLocation()
                        .subscribe({ result ->
                            if (result.status.equals("ok")) {
                                lbMessage?.text = "Success: ${result.message}"
                                listener.botLocationReceived(ip, result.data)
                            } else {
                                listener.botLocationReceived(ip, null)
                                lbMessage?.text = "Failed: ${result.message}"
                            }
                        }, { error ->
                            listener.botLocationFailed(ip)
                            lbMessage?.text = "Failed: ${error.message}"
                        })
            })
        }
    }
}

interface BallsListListener {
    fun ballsListReceived(ip: String, data: List<Ball>?)
    fun ballsListFailed(ip: String)
}

interface BotLocationListener {
    fun botLocationReceived(ip: String, data: BotLocation?)
    fun botLocationFailed(ip: String)
}

interface OpponentLocationListener {
    fun opponentLocationReceived(ip: String, data: List<Ball>)
    fun opponentLocationFailed(ip: String)
}