package main.utils

import main.forms.LogForm
import java.io.File
import java.util.concurrent.Executors

class Log {
    companion object {

        fun d(tag: String, message: String) {
            println("$tag *** $message")
        }

        fun d(message: String) {
            d("", message)
        }

        /*fun d(tag: String, message: String) {
            //Executors.newCachedThreadPool().submit {
            try {
                println("$tag *** $message")
                LogForm.logger.println("$tag *** $message\n")
                val file = File("logs/robo-dribble.txt")
                if (!file.exists()) {
                    File(file.parent).mkdir()
                }
                file.appendText("$tag *** $message\n")
            } catch (e: Exception) {
            }
            //}
        }

        fun d(tag: String) {
            d(tag, "\n")
        }

        fun d() {
            d("", "\n")
        }*/
    }
}