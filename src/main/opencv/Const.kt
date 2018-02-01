package main.opencv

import org.opencv.core.Scalar;

/**
 * Created by sakkeer on 12/12/17.
 */

class Const {

    companion object {

        val BALL_RADIUS_MIN = 10
        val BALL_RADIUS_MAX = 26
        val BALL_MIN_DISTANCE = 13
        var BALL_SCALAR_MIN = Scalar(20.0, 100.0, 100.0)
        var BALL_SCALAR_MAX = Scalar(35.0, 255.0, 255.0)

        val REFERENCE_RADIUS_MIN = 10
        val REFERENCE_RADIUS_MAX = 30
        val REFERENCE_MIN_DISTANCE = 100
        var REFERENCE_SCALAR_MIN = Scalar(12.0, 72.0, 108.0)
        var REFERENCE_SCALAR_MAX = Scalar(17.0, 146.0, 180.0)

        val BOT_FRONT_RADIUS_MIN = 26
        val BOT_FRONT_RADIUS_MAX = 100
        val BOT_FRONT_MIN_DISTANCE = 500
        var BOT_FRONT_SCALAR_MIN = Scalar(147.0, 169.0, 118.0)
        var BOT_FRONT_SCALAR_MAX = Scalar(155.0, 215.0, 136.0)

        val BOT_BACK_RADIUS_MIN = 26
        val BOT_BACK_RADIUS_MAX = 100
        val BOT_BACK_MIN_DISTANCE = 500
        var BOT_BACK_SCALAR_MIN = Scalar(63.0, 96.0, 136.0)
        var BOT_BACK_SCALAR_MAX = Scalar(73.0, 183.0, 255.0)

        private val BOT_LOCATOR_DISTANCE_IN_BETWEEN = 15
        private val BOT_LOCATOR_DISTANCE_TO_CORNER = 10.61
        val BOT_LOCATOR_DISTANCE_RATIO = BOT_LOCATOR_DISTANCE_TO_CORNER / BOT_LOCATOR_DISTANCE_IN_BETWEEN
        val BOT_LOCATOR_ANGLE_45 = 0.785398
        val BOT_LOCATOR_ANGLE_135 = 2.35619

    }
}
