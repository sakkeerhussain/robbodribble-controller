package main

import Jama.Matrix

fun main(args : Array<String>) {



//    val A = Matrix(arrayOf(doubleArrayOf(25.0, 5.0, 1.0), doubleArrayOf(1.0, 1.0 ,1.0), doubleArrayOf(9.0, 3.0, 1.0)))
//    val Y = Matrix(doubleArrayOf(5.0, 1.0, 3.0), 3)
//
//    val X = A.inverse().times(Y)
//
//    X.print(0, 0)



    val A = Matrix(arrayOf(doubleArrayOf(25.0, 9.0, 15.0, 5.0, 3.0, 1.0),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
            doubleArrayOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
            doubleArrayOf(1.0, 0.0, 0.0, 1.0, 0.0, 1.0),
            doubleArrayOf(0.0, 1.0, 0.0, 0.0, 1.0, 1.0),
            doubleArrayOf(4.0, 0.0, 0.0, 2.0, 0.0, 1.0)))
    val Y = Matrix(doubleArrayOf(5.0, 0.0, 1.0, 1.0, 0.0, 2.0), 6)

    val X = A.inverse().times(Y)

    X.print(0, 0)




}