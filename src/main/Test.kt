package main

import java.util.concurrent.Executors

fun main(args : Array<String>) {
    val future = Executors.newCachedThreadPool().submit {
        var c = 1;
        while (!Thread.currentThread().isInterrupted()){
            println("Reached ${c++}th iteration.")
            Thread.sleep(100)
        }
    }
    Thread.sleep(1000)
    future.cancel(true)
    println("Execution completed.")
}