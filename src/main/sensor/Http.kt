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
    }
}