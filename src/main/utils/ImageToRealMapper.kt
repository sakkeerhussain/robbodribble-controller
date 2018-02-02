package main.utils

import main.geometry.Point
import main.opencv.OpenCV
import Jama.Matrix



object ImageToRealMapper {

    var aX = 0
    var bX = 0
    var cX = 0
    var dX = 0
    var eX = 0
    var fX = 0

    var aY = 0
    var bY = 0
    var cY = 0
    var dY = 0
    var eY = 0
    var fY = 0

    fun updateMappingConstants() {
        val lhsArray = arrayOf(doubleArrayOf(25.0, 1.0, 5.0, 1.0, 5.0, 1.0), doubleArrayOf(4.0, 4.0, 2.0, 2.0, 4.0, 1.0), doubleArrayOf(24.0, 0.0, 4.0, 0.0, 4.0, 0.0), doubleArrayOf(6.0, 0.0, 1.0, 0.0, 0.0, 0.0), doubleArrayOf(1.0, 0.0, 1.0, 0.0, 0.0, 0.0), doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0))
        val rhsArray = doubleArrayOf(5.0, 2.0, 4.0, 1.0, 1.0, 0.0)

        //Point 1
        rhsArray.set(0, OpenCV.refPoint1.pointBord.x.toDouble())
        lhsArray.set(0, getArrayOfPoint(OpenCV.refPoint1.pointImage))


        //Creating Matrix Objects with arrays
        val lhs = Matrix(lhsArray)
        val rhs = Matrix(rhsArray, 6)

//Calculate Solved Matrix
        val ans = lhs.solve(rhs)

        aX = 4
    }

    private fun getArrayOfPoint(pointImage: Point): DoubleArray {
        return doubleArrayOf()
    }

    fun convertPointOnBoard(point: Point): Point {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return Point(4f, 7f)
    }

}