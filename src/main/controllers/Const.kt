package main.controllers

import main.geometry.Point
import main.utils.PathVertex

object Const {
    val POST = 1
    //val POST = 2
//    val BOT_ADDRESS = "10.7.120.81"
     val BOT_ADDRESS = "192.168.1.55"
//        val BOT_ADDRESS = "127.0.0.1:9000"

    val IP_NEXUS: String = "10.7.120.3"
    val IP_SAKKEER: String = "10.7.170.6"
    val IP_KOOKKAL: String = "10.7.170.19"


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
    val BOT_ALLOWED_DEVIATION = 15
    val BOT_ALLOWED_DEVIATION_FOR_BALLS = 15

    val POST_LOCATION: Point
    val POST_1_PATH = ArrayList<PathVertex>()

    init {
        if (POST == 1)
            POST_LOCATION = Point(0f, 90f)
        else
            POST_LOCATION = Point(280f, 90f)


        initPost1Path()
    }

    private fun initPost1Path() {
        POST_1_PATH.add(PathVertex(Point(30f, 30f)))
        POST_1_PATH.add(PathVertex(Point(15f, 120f)))
        POST_1_PATH.add(PathVertex(Point(15f, 90f), false))
        POST_1_PATH.add(PathVertex(Point(20f, 120f)))
        POST_1_PATH.add(PathVertex(Point(5f, 90f), false))
        POST_1_PATH.add(PathVertex(Point(-1f, -1f)))
    }


    object FileName {
        const val BOT_STATUS = "config/controllers/bot_status.json"
    }

}