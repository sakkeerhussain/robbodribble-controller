package main

import main.controllers.Ball
import main.controllers.BallsManager
import main.geometry.Point
import main.utils.Log

fun main(args : Array<String>) {
    val TAG = "Test"
    Log.d("\nBall manager update ball test")

    val balls1 = ArrayList<Ball>()
    balls1.add(Ball(Point(10f, 10f)))
    balls1.add(Ball(Point(20f, 20f)))
    BallsManager.get().updateBallsList(balls1)
    Log.d(TAG, BallsManager.get().getBallList().toString())


    val balls2 = ArrayList<Ball>()
    balls2.add(Ball(Point(10f, 10f)))
    BallsManager.get().updateBallsList(balls2)
    BallsManager.get().updateBallsList(balls2)
    BallsManager.get().updateBallsList(balls2)
    Log.d(TAG, BallsManager.get().getBallList().toString())

    val balls3 = ArrayList<Ball>()
    balls3.add(Ball(Point(20f, 20f)))
    BallsManager.get().updateBallsList(balls3)
    BallsManager.get().updateBallsList(balls3)
    BallsManager.get().updateBallsList(balls3)
    Log.d(TAG, BallsManager.get().getBallList().toString())

}