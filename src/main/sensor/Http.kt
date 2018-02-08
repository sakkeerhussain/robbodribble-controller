package main.sensor

import java.util.concurrent.Executors
import javax.swing.JLabel
import javax.swing.JTextField

class Http {
    companion object {
        fun calibrateRef(ip: String, port: String, point: Int, lbMessage: JLabel, runnable: Runnable) {
            Executors.newCachedThreadPool().submit({
                lbMessage.text = "Loading..."
                ApiService.Factory.create("SENSOR - CALIBRATE   ", ip, port).calibrateRef(point)
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

        fun getReferencePoint(ip: String, port: String, point: Int, lbMessage: JLabel, tfX: JTextField, tfY: JTextField) {
            Executors.newCachedThreadPool().submit({
                lbMessage.text = "Loading..."
                ApiService.Factory.create("SENSOR - CALIBRATE   ", ip, port).getReferencePoint(point)
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

        fun setReferencePoint(ip: String, port: String, point: Int, lbMessage: JLabel, xImage: Float, yImage: Float,
                              xBord: Float, yBord: Float, runnable: Runnable) {
            Executors.newCachedThreadPool().submit({
                lbMessage.text = "Loading..."
                ApiService.Factory.create("SENSOR - CALIBRATE   ", ip, port).setReferencePoint(point, xImage, yImage, xBord, yBord)
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

        fun getBalls(sensor: Sensor, lbMessage: JLabel?, listener: BallsListListener) {
            Executors.newCachedThreadPool().submit({
                lbMessage?.text = "Loading balls..."
                ApiService.Factory.create("SENSOR - BALLS           ", sensor.ip, sensor.port)
                        .getBalls()
                        .subscribe({ result ->
                            if (result.status.equals("ok")) {
                                lbMessage?.text = "Success: ${result.message}"
                                listener.ballsListReceived(sensor, result.data)
                            } else {
                                listener.ballsListReceived(sensor,null)
                                lbMessage?.text = "Failed: ${result.message}"
                            }
                        }, { error ->
                            listener.ballsListFailed(sensor)
                            lbMessage?.text = "Failed: ${error.message}"
                        })
            })
        }

        fun getBotLocation(ip: String, port: String, lbMessage: JLabel?, listener: BotLocationListener) {
            Executors.newCachedThreadPool().submit({
                lbMessage?.text = "Loading bot location..."
                ApiService.Factory.create("SENSOR-BOT LOCATION", ip, port).getBotLocation()
                        .subscribe({ result ->
                            if (result.status.equals("ok")) {
                                lbMessage?.text = "Success: ${result.message}"
                                listener.botLocationReceived(ip, port, result.data)
                            } else {
                                listener.botLocationReceived(ip, port, null)
                                lbMessage?.text = "Failed: ${result.message}"
                            }
                        }, { error ->
                            listener.botLocationFailed(ip, port)
                            lbMessage?.text = "Failed: ${error.message}"
                        })
            })
        }
    }
}