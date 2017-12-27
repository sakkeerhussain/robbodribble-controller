package main.sensor

import javax.swing.JLabel

class Http {
    companion object {
        fun calibrateRef(point: Int, lbMessage: JLabel) {
            lbMessage.text = "Loading..."
            ApiService.Factory.create().calibrateRef(point)
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            lbMessage.text = "Success: ${result.message}"
                        }else{
                            lbMessage.text = "Failed: ${result.message}"
                        }
                    }, { error ->
                        lbMessage.text = "Failed: ${error.message}"
                    })
        }

        fun getReferencePoint(point: Int, lbMessage: JLabel, lbPoint: JLabel) {
            lbMessage.text = "Loading..."
            ApiService.Factory.create().getReferencePoint(point)
                    .subscribe({ result ->
                        if (result.status.equals("ok")) {
                            lbMessage.text = "Success: ${result.message}"
                            lbPoint.text = result.data.value.toString()
                        }else{
                            lbMessage.text = "Failed: ${result.message}"
                            lbPoint.text = "----"
                        }
                    }, { error ->
                        lbMessage.text = "Failed: ${error.message}"
                        lbPoint.text = "----"
                    })
        }
    }
}