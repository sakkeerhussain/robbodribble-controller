package main.utils

import main.geometry.Point
import main.opencv.OpenCV
import Jama.Matrix
import main.opencv.models.ReferencePoint




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

        println("\n\nPrinting coefficients")
        println("aX: ${"%.3f".format(aX.toFloat())}")
        println("bX: ${"%.3f".format(bX.toFloat())}")
        println("cX: ${"%.3f".format(cX.toFloat())}")
        println("dX: ${"%.3f".format(dX.toFloat())}")
        println("eX: ${"%.3f".format(eX.toFloat())}")
        println("fX: ${"%.3f".format(fX.toFloat())}")

        println("aY: ${"%.3f".format(aY.toFloat())}")
        println("bY: ${"%.3f".format(bY.toFloat())}")
        println("cY: ${"%.3f".format(cY.toFloat())}")
        println("dY: ${"%.3f".format(dY.toFloat())}")
        println("eY: ${"%.3f".format(eY.toFloat())}")
        println("fY: ${"%.3f".format(fY.toFloat())}")
    }

    private fun updateMappingConstantsX() {
        val aArray = Array<DoubleArray>(9, { DoubleArray(6) })
        val yArray = DoubleArray(9)

        //Point 1
        yArray[0] = OpenCV.boardReference.refPoint1.pointBord.x.toDouble()
        aArray[0] = getArrayOfPoint(OpenCV.boardReference.refPoint1.pointImage)

        //Point 12
        yArray[1] = OpenCV.boardReference.refPointMid12.pointBord.x.toDouble()
        aArray[1] = getArrayOfPoint(OpenCV.boardReference.refPointMid12.pointImage)

        //Point 2
        yArray[2] = OpenCV.boardReference.refPoint2.pointBord.x.toDouble()
        aArray[2] = getArrayOfPoint(OpenCV.boardReference.refPoint2.pointImage)

        //Point 4
        yArray[3] = OpenCV.boardReference.refPoint4.pointBord.x.toDouble()
        aArray[3] = getArrayOfPoint(OpenCV.boardReference.refPoint4.pointImage)

        //Point 34
        yArray[4] = OpenCV.boardReference.refPointMid34.pointBord.x.toDouble()
        aArray[4] = getArrayOfPoint(OpenCV.boardReference.refPointMid34.pointImage)

        //Point 3
        yArray[5] = OpenCV.boardReference.refPoint3.pointBord.x.toDouble()
        aArray[5] = getArrayOfPoint(OpenCV.boardReference.refPoint3.pointImage)

        //Point C1
        yArray[6] = OpenCV.boardReference.refPointC.pointBord.x.toDouble()
        aArray[6] = getArrayOfPoint(OpenCV.boardReference.refPointC.pointImage)

        //Point Q1
        yArray[7] = OpenCV.boardReference.refPointQ1.pointBord.x.toDouble()
        aArray[7] = getArrayOfPoint(OpenCV.boardReference.refPointQ1.pointImage)

        //Point Q2
        yArray[8] = OpenCV.boardReference.refPointQ2.pointBord.x.toDouble()
        aArray[8] = getArrayOfPoint(OpenCV.boardReference.refPointQ2.pointImage)


        //Creating Matrix Objects with arrays
        val a = Matrix(aArray)
        val y = Matrix(yArray, 9)

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
        val aArray = Array<DoubleArray>(9, { DoubleArray(6) })
        val yArray = DoubleArray(9)

        //Point 1
        yArray[0] = OpenCV.boardReference.refPoint1.pointBord.y.toDouble()
        aArray[0] = getArrayOfPoint(OpenCV.boardReference.refPoint1.pointImage)

        //Point 12
        yArray[1] = OpenCV.boardReference.refPointMid12.pointBord.y.toDouble()
        aArray[1] = getArrayOfPoint(OpenCV.boardReference.refPointMid12.pointImage)

        //Point 2
        yArray[2] = OpenCV.boardReference.refPoint2.pointBord.y.toDouble()
        aArray[2] = getArrayOfPoint(OpenCV.boardReference.refPoint2.pointImage)

        //Point 4
        yArray[3] = OpenCV.boardReference.refPoint4.pointBord.y.toDouble()
        aArray[3] = getArrayOfPoint(OpenCV.boardReference.refPoint4.pointImage)

        //Point 34
        yArray[4] = OpenCV.boardReference.refPointMid34.pointBord.y.toDouble()
        aArray[4] = getArrayOfPoint(OpenCV.boardReference.refPointMid34.pointImage)

        //Point 3
        yArray[5] = OpenCV.boardReference.refPoint3.pointBord.y.toDouble()
        aArray[5] = getArrayOfPoint(OpenCV.boardReference.refPoint3.pointImage)

        //Point C1
        yArray[6] = OpenCV.boardReference.refPointC.pointBord.y.toDouble()
        aArray[6] = getArrayOfPoint(OpenCV.boardReference.refPointC.pointImage)

        //Point Q1
        yArray[7] = OpenCV.boardReference.refPointQ1.pointBord.y.toDouble()
        aArray[7] = getArrayOfPoint(OpenCV.boardReference.refPointQ1.pointImage)

        //Point Q2
        yArray[8] = OpenCV.boardReference.refPointQ2.pointBord.y.toDouble()
        aArray[8] = getArrayOfPoint(OpenCV.boardReference.refPointQ2.pointImage)

        val a = Matrix(aArray)
        val y = Matrix(yArray, 9)
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

    //Assumption #2
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

    //Assumption #1
    fun convertPointToBoard2(centerPoint: Point): Point {
        val point1 = OpenCV.boardReference.refPoint1
        val point2 = OpenCV.boardReference.refPoint2
        val point3 = OpenCV.boardReference.refPoint3

        val imageXd = centerPoint.x - point1.pointImage.x
        val imageYd = centerPoint.y - point1.pointImage.y

        val imageXD = point2.pointImage.x - point1.pointImage.x
        val imageYD = point3.pointImage.y - point1.pointImage.y

        val boardXD = point2.pointBord.x - point1.pointBord.x
        val boardYD = point3.pointBord.y - point1.pointBord.y

        var x = imageXd * boardXD / imageXD
        var y = imageYd * boardYD / imageYD

        x += point1.pointBord.x
        y += point1.pointBord.y
        return Point(x, y)
    }
}