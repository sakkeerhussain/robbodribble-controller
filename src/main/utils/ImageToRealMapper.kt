package main.utils

import main.geometry.Point
import main.opencv.OpenCV
import Jama.Matrix


object ImageToRealMapper {

    private var aX = 0.0
    private var bX = 0.0
    private var cX = 0.0
    private var dX = 0.0
    private var eX = 0.0
    private var fX = 0.0

    private var aY = 0.0
    private var bY = 0.0
    private var cY = 0.0
    private var dY = 0.0
    private var eY = 0.0
    private var fY = 0.0

    fun updateMappingConstants() {
        updateMappingConstantsX()
        updateMappingConstantsY()
    }

    private fun updateMappingConstantsX() {
        val aArray = Array<DoubleArray>(6, { DoubleArray(6) })
        val yArray = DoubleArray(6)

        //Point 1
        yArray[0] = OpenCV.refPoint1.pointBord.x.toDouble()
        aArray[0] = getArrayOfPoint(OpenCV.refPoint1.pointImage)

        //Point 12
        yArray[1] = OpenCV.refPointMid12.pointBord.x.toDouble()
        aArray[1] = getArrayOfPoint(OpenCV.refPointMid12.pointImage)

        //Point 2
        yArray[2] = OpenCV.refPoint2.pointBord.x.toDouble()
        aArray[2] = getArrayOfPoint(OpenCV.refPoint2.pointImage)

        //Point 4
        yArray[3] = OpenCV.refPoint4.pointBord.x.toDouble()
        aArray[3] = getArrayOfPoint(OpenCV.refPoint4.pointImage)

        //Point 34
        yArray[4] = OpenCV.refPointMid34.pointBord.x.toDouble()
        aArray[4] = getArrayOfPoint(OpenCV.refPointMid34.pointImage)

        //Point 3
        yArray[5] = OpenCV.refPoint3.pointBord.x.toDouble()
        aArray[5] = getArrayOfPoint(OpenCV.refPoint3.pointImage)


        //Creating Matrix Objects with arrays
        val a = Matrix(aArray)
        val y = Matrix(yArray, 6)

        //Calculate Solved Matrix
        val ans = a.inverse().times(y)

        aX = ans.get(0, 0)
        bX = ans.get(1, 0)
        cX = ans.get(2, 0)
        dX = ans.get(3, 0)
        eX = ans.get(4, 0)
        fX = ans.get(5, 0)
    }

    private fun updateMappingConstantsY() {
        val aArray = Array<DoubleArray>(6, { DoubleArray(6) })
        val yArray = DoubleArray(6)

        //Point 1
        yArray[0] = OpenCV.refPoint1.pointBord.y.toDouble()
        aArray[0] = getArrayOfPoint(OpenCV.refPoint1.pointImage)

        //Point 12
        yArray[1] = OpenCV.refPointMid12.pointBord.y.toDouble()
        aArray[1] = getArrayOfPoint(OpenCV.refPointMid12.pointImage)

        //Point 2
        yArray[2] = OpenCV.refPoint2.pointBord.y.toDouble()
        aArray[2] = getArrayOfPoint(OpenCV.refPoint2.pointImage)

        //Point 4
        yArray[3] = OpenCV.refPoint4.pointBord.y.toDouble()
        aArray[3] = getArrayOfPoint(OpenCV.refPoint4.pointImage)

        //Point 34
        yArray[4] = OpenCV.refPointMid34.pointBord.y.toDouble()
        aArray[4] = getArrayOfPoint(OpenCV.refPointMid34.pointImage)

        //Point 3
        yArray[5] = OpenCV.refPoint3.pointBord.y.toDouble()
        aArray[5] = getArrayOfPoint(OpenCV.refPoint3.pointImage)

        val a = Matrix(aArray)
        val y = Matrix(yArray, 6)
        val ans = a.inverse().times(y)

        aY = ans.get(0, 0)
        bY = ans.get(1, 0)
        cY = ans.get(2, 0)
        dY = ans.get(3, 0)
        eY = ans.get(4, 0)
        fY = ans.get(5, 0)
    }

    private fun getArrayOfPoint(point: Point): DoubleArray {
        return doubleArrayOf(Math.pow(point.x.toDouble(), 2.0),
                Math.pow(point.y.toDouble(), 2.0),
                (point.x * point.y).toDouble(),
                point.x.toDouble(),
                point.y.toDouble(),
                1.0)
    }

    fun convertPointToBoard(point: Point): Point {
        val x2 = Math.pow(point.x.toDouble(), 2.0)
        val y2 = Math.pow(point.y.toDouble(), 2.0)
        val xy = (point.x * point.y).toDouble()
        val x = point.x.toDouble()
        val y = point.y.toDouble()
        val pX = aX * x2 + bX * y2 + cX * xy + dX * x + eX * y + fX
        val pY = aY * x2 + bY * y2 + cY * xy + dY * x + eY * y + fY
        return Point(pX.toFloat(), pY.toFloat())
    }

}