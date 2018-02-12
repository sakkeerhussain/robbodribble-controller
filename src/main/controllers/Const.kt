package main.controllers

import main.geometry.Point
import main.utils.PathVertex

object Const {
    val POST = 1
    //val POST = 2
    val BOT_ADDRESS = "10.7.120.81"
    //val BOT_ADDRESS = "192.168.1.55"
    //val BOT_ADDRESS = "127.0.0.1:9000"

    val IP_NEXUS: String = "10.7.120.3"
    val IP_SAKKEER: String = "10.7.170.6"
    val IP_KOOKKAL: String = "10.7.170.19"


    val BALL_RANK_DISTANCE_CONSTANT = -1
    val BALL_RANK_ANGLE_CONSTANT = 1
    val BALL_RANK_SENSOR_CONSTANT = 1

    val RAD_TO_DEGREE = 57.2958

    const val PATH_LEFT = "left"
    const val PATH_RIGHT = "right"
    const val PATH_FORWARD = "forward"
    const val PATH_BACKWARD = "backward"

    const val BOT_WIDTH = 30
    const val BOT_MAX_BALL_CAPACITY = 1
    const val BOT_MIN_DIST_IN_UNIT_TIME = 3
    const val BOT_ALLOWED_DEVIATION = 15
    const val BOT_ALLOWED_DEVIATION_FOR_BALLS = 15

    val POST_PATH_1 = ArrayList<PathVertex>()
    val POST_PATH_2 = ArrayList<PathVertex>()

    init {
        initPostPath1()
        initPostPath2()
    }

    private fun initPostPath1() {
        POST_PATH_1.add(PathVertex(Point(30f, 30f)))
        POST_PATH_1.add(PathVertex(Point(15f, 120f)))
        POST_PATH_1.add(PathVertex(Point(15f, 90f), false))
        POST_PATH_1.add(PathVertex(Point(20f, 90f)))
        POST_PATH_1.add(PathVertex(Point(5f, 90f), false))
        POST_PATH_1.add(PathVertex(Point(-1f, -1f)))
    }

    private fun initPostPath2() {
        POST_PATH_2.add(PathVertex(Point(30f, 150f)))
        POST_PATH_2.add(PathVertex(Point(15f, 60f)))
        POST_PATH_2.add(PathVertex(Point(15f, 90f), false))
        POST_PATH_2.add(PathVertex(Point(20f, 90f)))
        POST_PATH_2.add(PathVertex(Point(5f, 90f), false))
        POST_PATH_2.add(PathVertex(Point(-1f, -1f)))
    }


    object FileName {
        const val BOT_STATUS = "config/controllers/bot_status.json"
    }

}