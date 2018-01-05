package main

import main.controllers.Ball
import main.controllers.BallsManager

fun main(args : Array<String>) {
    println("\nBall manager update ball test")

    val balls1 = ArrayList<Ball>()
    balls1.add(Ball(10f, 10f))
    balls1.add(Ball(20f, 20f))
    BallsManager.get().updateBallsList(balls1)
    println(BallsManager.get().getBallList())


    val balls2 = ArrayList<Ball>()
    balls2.add(Ball(10f, 10f))
    BallsManager.get().updateBallsList(balls2)
    BallsManager.get().updateBallsList(balls2)
    BallsManager.get().updateBallsList(balls2)
    println(BallsManager.get().getBallList())

    val balls3 = ArrayList<Ball>()
    balls3.add(Ball(20f, 20f))
    BallsManager.get().updateBallsList(balls3)
    BallsManager.get().updateBallsList(balls3)
    BallsManager.get().updateBallsList(balls3)
    println(BallsManager.get().getBallList())

}