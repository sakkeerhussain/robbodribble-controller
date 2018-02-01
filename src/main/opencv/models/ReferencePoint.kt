package main.opencv.models

import main.geometry.Point

class ReferencePoint(val pointImage: Point, val pointBord: Point) {

    constructor(pointImageX: Float, pointImageY: Float, pointBordX: Float, pointBordY: Float) :
            this(Point(pointImageX, pointImageY), Point(pointBordX, pointBordY))
}