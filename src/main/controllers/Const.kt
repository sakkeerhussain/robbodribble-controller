package main.controllers

import main.controllers.bot.BotControllerSweep
import main.geometry.Point
import main.utils.PathVertex

object Const {

    val BOT_ADDRESS = "10.7.120.81"
    //val BOT_ADDRESS = "192.168.1.55"
    //val BOT_ADDRESS = "127.0.0.1:9000"

    val IP_NEXUS: String = "10.7.120.3"
    val IP_SAKKEER: String = "10.7.170.6"
    val IP_KOOKKAL: String = "10.7.170.19"

    const val BALL_LOCATION_TOLERANCE = 6
    const val BALL_NOT_FOUND_TOLERANCE = 2

    const val BALL_RANK_DISTANCE_CONSTANT = -1
    const val BALL_RANK_ANGLE_CONSTANT = -60
    const val BALL_RANK_SENSOR_CONSTANT = 0.05

    val RAD_TO_DEGREE = 57.2958

    const val PATH_LEFT = "left"
    const val PATH_RIGHT = "right"
    const val PATH_FORWARD = "forward"
    const val PATH_BACKWARD = "backward"

    const val BOT_WIDTH = 30
    const val BOT_MAX_BALL_CAPACITY = 5
    const val BOT_MIN_DIST_IN_UNIT_TIME = 3
    const val BOT_ALLOWED_DEVIATION = 25
    const val BOT_ALLOWED_DEVIATION_FOR_BALLS = 15

    val POST_PATH_1 = ArrayList<PathVertex>()
    val POST_PATH_2 = ArrayList<PathVertex>()

    init {
        initPostPath1()
        initPostPath2()
    }

    private fun initPostPath1() {

        POST_PATH_1.add(PathVertex(Point(60f, 50f), true))
        POST_PATH_1.add(PathVertex(Point(20f, 50f), true))
        POST_PATH_1.add(PathVertex(Point(10f, 95f), true))
        POST_PATH_1.add(PathVertex(Point(40f, 90f), true))
        POST_PATH_1.add(PathVertex(Point(4f, 90f), false))
        POST_PATH_1.add(PathVertex(Const.PATH_BACKWARD, 20))
        POST_PATH_1.add(PathVertex(Point(-1f, -1f), true)) //Open Door

        /*POST_PATH_2.add(PathVertex(Point(10f, 90f)))
        POST_PATH_1.add(PathVertex(Point(1f, 90f), false))
        POST_PATH_1.add(PathVertex(Point(-1f, -1f)))*/
    }

    private fun initPostPath2() {

        POST_PATH_2.add(PathVertex(Point(60f, 130f), true))
        POST_PATH_2.add(PathVertex(Point(20f, 130f), true))
        POST_PATH_2.add(PathVertex(Point(10f, 85f), true))
        POST_PATH_2.add(PathVertex(Point(40f, 90f), true))
        POST_PATH_2.add(PathVertex(Point(4f, 90f), false))
        POST_PATH_1.add(PathVertex(Const.PATH_BACKWARD, 20))
        POST_PATH_2.add(PathVertex(Point(-1f, -1f), true)) //Open Door
    }


    object FileName {
        const val BOT_STATUS = "config/controllers/bot_status.json"
    }

}