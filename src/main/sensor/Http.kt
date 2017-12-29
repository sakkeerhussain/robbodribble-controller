package main.sensor

import javax.swing.JLabel
import javax.swing.JTextField

class Http {
    companion object {
        fun calibrateRef(ip: String, point: Int, lbMessage: JLabel, runnable: Runnable) {
            lbMessage.text = "Loading..."
            ApiService.Factory.create(ip).calibrateRef(point)
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            lbMessage.text = "Success: ${result.message}"
                            runnable.run()
                        }else{
                            lbMessage.text = "Failed: ${result.message}"
                        }
                    }, { error ->
                        lbMessage.text = "Failed: ${error.message}"
                    })
        }

        fun getReferencePoint(ip: String, point: Int, lbMessage: JLabel, tfX: JTextField, tfY: JTextField) {
            lbMessage.text = "Loading..."
            ApiService.Factory.create(ip).getReferencePoint(point)
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            lbMessage.text = "Success: ${result.message}"
                            tfX.text = result.data.value.x.toString()
                            tfY.text = result.data.value.y.toString()
                        }else{
                            lbMessage.text = "Failed: ${result.message}"
                            tfX.text = ""
                            tfY.text = ""
                        }
                    }, { error ->
                        lbMessage.text = "Failed: ${error.message}"
                        tfX.text = ""
                        tfY.text = ""
                    })
        }

        fun setReferencePoint(ip: String, point: Int, lbMessage: JLabel, x: Float, y: Float, runnable: Runnable) {
            lbMessage.text = "Loading..."
            ApiService.Factory.create(ip).setReferencePoint(point, x, y)
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            lbMessage.text = "Success: ${result.message}"
                            runnable.run()
                        }else{
                            lbMessage.text = "Failed: ${result.message}"
                        }
                    }, { error ->
                        lbMessage.text = "Failed: ${error.message}"
                    })
        }
    }
}