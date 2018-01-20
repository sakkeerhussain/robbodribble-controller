package main.controllers

import main.geometry.Point

class Const{
    companion object {
        val POST = 1
        //val POST = 2
        val BOT_ADDRESS = "10.7.120.81"
//        val BOT_ADDRESS = "127.0.0.1:8080"
//        val BOT_ADDRESS = "127.0.0.1:9000"

        val IP_NEXUS: String = "10.7.120.3"
        val IP_SAKKEER: String = "10.7.170.6"


        val BALL_RANK_DISTANCE_CONSTANT = -1
        val BALL_RANK_ANGLE_CONSTANT = 1
        val BALL_RANK_SENSOR_CONSTANT = 1

        val RAD_TO_DEGREE = 57.2958

        val PATH_LEFT = "left"
        val PATH_RIGHT = "right"
        val PATH_FORWARD = "forward"
        val PATH_BACKWARD = "backward"

        val BOT_WIDTH = 30
        val BOT_MAX_BALL_CAPACITY = 1
        val BOT_MIN_DIST_IN_UNIT_TIME = 3
        val BOT_ALLOWED_DEVIATION = 8

        val POST_LOCATION: Point
        init {
            if (POST == 1)
                POST_LOCATION = Point(90f, 0f)
            else
                POST_LOCATION = Point(90f, 280f)
        }
    }
}