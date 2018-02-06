package main

import Jama.Matrix
import main.geometry.Point
import main.opencv.OpenCV
import main.opencv.models.ReferencePoint
import main.utils.ImageToRealMapper

fun main(args : Array<String>) {

//    val A = Matrix(arrayOf(doubleArrayOf(25.0, 5.0, 1.0), doubleArrayOf(1.0, 1.0 ,1.0), doubleArrayOf(9.0, 3.0, 1.0)))
//    val Y = Matrix(doubleArrayOf(5.0, 1.0, 3.0), 3)
//    val X = A.inverse().times(Y)
//    X.print(0, 0)


//    val A = Matrix(arrayOf(doubleArrayOf(25.0, 9.0, 15.0, 5.0, 3.0, 1.0),
//            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
//            doubleArrayOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
//            doubleArrayOf(1.0, 0.0, 0.0, 1.0, 0.0, 1.0),
//            doubleArrayOf(0.0, 1.0, 0.0, 0.0, 1.0, 1.0),
//            doubleArrayOf(4.0, 0.0, 0.0, 2.0, 0.0, 1.0)))
//    val Y = Matrix(doubleArrayOf(5.0, 0.0, 1.0, 1.0, 0.0, 2.0), 6)
//    val X = A.inverse().times(Y)
//    X.print(0, 0)

    OpenCV.init()
//    OpenCV.refPoint1 = ReferencePoint(10,5,10,3)
//    OpenCV.refPointMid12 = ReferencePoint(20,16,20,8)
//    OpenCV.refPoint2 = ReferencePoint(8,4,8,2)
//    OpenCV.refPoint4 = ReferencePoint(4,4,4,2)
//    OpenCV.refPointMid34 = ReferencePoint(3f,4f,3f,2f)
//    OpenCV.refPoint3 = ReferencePoint(5f,2f,5f,1f)

//    OpenCV.refPoint1 = ReferencePoint(0.01f, 0.01f, -10f, -10f)
//    OpenCV.refPointMid12 = ReferencePoint(622.5f, 0.01f, 140f, -10f)
//    OpenCV.refPoint2 = ReferencePoint(1245f, 0.01f, 290f, -10f)
//    OpenCV.refPoint3 = ReferencePoint(0.01f, 865f, -10f, 190f)
//    OpenCV.refPointMid34 = ReferencePoint(622.5f, 865f, 140f, 190f)
//    OpenCV.refPoint4 = ReferencePoint(1245f, 865f, 290f, 190f)
//    ImageToRealMapper.updateMappingConstants()

//    OpenCV.refPoint1 = ReferencePoint(1f, 1f, -10f, -10f)
//    OpenCV.refPointMid12 = ReferencePoint(600f, 1f, 140f, -10f)
//    OpenCV.refPoint2 = ReferencePoint(1200f, 1f, 290f, -10f)
//    OpenCV.refPoint3 = ReferencePoint(1f, 800f, -10f, 190f)
//    OpenCV.refPointMid34 = ReferencePoint(600f, 800f, 140f, 190f)
//    OpenCV.refPoint4 = ReferencePoint(1200f, 800f, 290f, 190f)
//    ImageToRealMapper.updateMappingConstants()

    OpenCV.refPoint1 = ReferencePoint(1f, 1f, 0f, 0f)
    OpenCV.refPointMid12 = ReferencePoint(6f, 1f, 5f, 0f)
    OpenCV.refPoint2 = ReferencePoint(12f, 1f, 11f, 0f)
    OpenCV.refPoint3 = ReferencePoint(1f, 8f, 0f, 7f)
    OpenCV.refPointMid34 = ReferencePoint(6f, 8f, 5f, 7f)
    OpenCV.refPoint4 = ReferencePoint(12f, 8f, 11f, 7f)
    ImageToRealMapper.updateMappingConstants()

//    val point1 = Point(0.01f, 0.01f)
//    print("Map of $point1: ${ImageToRealMapper.convertPointToBoard(point1)}\n")
//    val point2 = Point(622.5f, 12.5f)
//    print("Map of $point2: ${ImageToRealMapper.convertPointToBoard(point2)}\n")
//    val point3 = Point(1245f, 25f)
//    print("Map of $point3: ${ImageToRealMapper.convertPointToBoard(point3)}\n")
//    val point4 = Point(0.01f, 865f)
//    print("Map of $point4: ${ImageToRealMapper.convertPointToBoard(point4)}\n")
//    val point5 = Point(622.5f, 852.5f)
//    print("Map of $point5: ${ImageToRealMapper.convertPointToBoard(point5)}\n")
//    val point6 = Point(1245f, 840f)
//    print("Map of $point6: ${ImageToRealMapper.convertPointToBoard(point6)}\n")


    val pointCenter = Point(600f, 420f)
    print("Map of $pointCenter: ${ImageToRealMapper.convertPointToBoard(pointCenter)}\n")

    println()
    println()
    for (i in 1..249) {
        val point7 = Point(i.toFloat(), 1f)
        print("Map of $point7: ${ImageToRealMapper.convertPointToBoard(point7)}\n")
    }

}