package main.sensor

import main.sensor.response.Ball
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

        fun getBalls(ip: String, lbMessage: JLabel?, listener: BallsListResponse) {
            Executors.newCachedThreadPool().submit({
                lbMessage?.text = "Loading balls..."
                ApiService.Factory.create(ip).getBalls()
                        .subscribe({ result ->
                            if (result.status.equals("ok")) {
                                lbMessage?.text = "Success: ${result.message}"
                                listener.ballsListReceived(ip, result.data)
                            } else {
                                listener.ballsListFailed(ip)
                                lbMessage?.text = "Failed: ${result.message}"
                            }
                        }, { error ->
                            listener.ballsListFailed(ip)
                            lbMessage?.text = "Failed: ${error.message}"
                        })
            })
        }
    }
}

interface BallsListResponse {
    fun ballsListReceived(ip: String, data: List<Ball>)
    fun ballsListFailed(ip: String)
}