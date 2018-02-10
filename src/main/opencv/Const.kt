package main.opencv

import org.opencv.core.Scalar;

/**
 * Created by sakkeer on 12/12/17.
 */

object Const {

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

        internal val BOT_FRONT_RADIUS_MIN = 26
        internal val BOT_FRONT_RADIUS_MAX = 50
        internal val BOT_FRONT_MIN_DISTANCE = 500
        internal var BOT_FRONT_SCALAR_MIN = Scalar(150.0, 117.0, 36.0)
        internal var BOT_FRONT_SCALAR_MAX = Scalar(163.0, 255.0, 159.0)

        internal val BOT_BACK_RADIUS_MIN = 26
        internal val BOT_BACK_RADIUS_MAX = 50
        internal val BOT_BACK_MIN_DISTANCE = 500
        internal var BOT_BACK_SCALAR_MIN = Scalar(57.0, 75.0, 68.0)
        internal var BOT_BACK_SCALAR_MAX = Scalar(70.0, 255.0, 199.0)

        private val BOT_LOCATOR_DISTANCE_IN_BETWEEN = 15
        private val BOT_LOCATOR_DISTANCE_TO_CORNER = 10.61
        val BOT_LOCATOR_DISTANCE_RATIO = BOT_LOCATOR_DISTANCE_TO_CORNER / BOT_LOCATOR_DISTANCE_IN_BETWEEN
        val BOT_LOCATOR_ANGLE_45 = 0.785398
        val BOT_LOCATOR_ANGLE_135 = 2.35619

    object FileName {
        const val REF_POINT = "config/reference_point_values.txt"
    }
}
