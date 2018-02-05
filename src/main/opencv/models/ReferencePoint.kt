package main.opencv.models

import main.geometry.Point

class ReferencePoint(val pointImage: Point, val pointBord: Point) {

    constructor(pointImageX: Float, pointImageY: Float, pointBordX: Float, pointBordY: Float) :
            this(Point(pointImageX, pointImageY), Point(pointBordX, pointBordY))

    constructor(pointImageX: Int, pointImageY: Int, pointBordX: Int, pointBordY: Int) :
            this(pointImageX.toFloat(), pointImageY.toFloat(), pointBordX.toFloat(), pointBordY.toFloat())
}