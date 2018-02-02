package main.opencv

import main.controllers.BotLocation
import main.geometry.Circle
import main.geometry.Line
import main.geometry.Point
import main.utils.Log
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO


object OpenCvUtils {
    val TAG = "OpenCvUtils"

    private fun convertPointOnBoard(point: Point): Point {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getBotLocationOnBoard(): BotLocation? {
        val location = getBotLocation() ?: return null
        return BotLocation(location.angle,
                convertPointOnBoard(location.backLeft),
                convertPointOnBoard(location.frontLeft),
                convertPointOnBoard(location.backRight),
                convertPointOnBoard(location.frontRight))
    }

    private fun getBotLocation(): BotLocation? {
        val startTime = Date().getTime()
        var frame = OpenCV.getFrame()
        if (frame == null) {
            Log.d(TAG, "No frame received")
            return null
        }
        frame = OpenCV.clipFrame(frame)
        val frontCircles = getBotFront(frame)
        val backCircles = getBotBack(frame)
        if (frontCircles.size < 1 || backCircles.size < 1) {
            Log.d(TAG, "Bot not found")
            return null
        } else if (frontCircles.size > 1 || backCircles.size > 1) {
            TODO("Handle multiple bot detection issue")
        }
        val frontCenter = frontCircles.get(0).center
        val backCenter = backCircles.get(0).center
        val centerLine = Line(frontCenter, backCenter)
        val centerLineAngle = centerLine.angle()
        val centerToCornerLength = (centerLine.length() * Const.BOT_LOCATOR_DISTANCE_RATIO).toFloat()
        var highest = true
        if (frontCenter.x < backCenter.x)
            highest = false
        val frontLeft = frontCenter.getPointAtAngle(centerLineAngle + Const.BOT_LOCATOR_ANGLE_45, centerToCornerLength, highest)
        val frontRight = frontCenter.getPointAtAngle(centerLineAngle - Const.BOT_LOCATOR_ANGLE_45, centerToCornerLength, highest)
        val backLeft = backCenter.getPointAtAngle(centerLineAngle + Const.BOT_LOCATOR_ANGLE_135, centerToCornerLength, highest)
        val backRight = backCenter.getPointAtAngle(centerLineAngle - Const.BOT_LOCATOR_ANGLE_135, centerToCornerLength, highest)
        Log.d("Bot detection", "Time taken to detect bot: " + (Date().getTime() - startTime) + "ms")
        return BotLocation(centerLineAngle, backLeft, backRight, frontLeft, frontRight)
    }

    private fun getBotFront(rgbaFrame: Mat): List<Circle> {
        return detectCircles("front.jpg", rgbaFrame, Const.BOT_FRONT_SCALAR_MIN, Const.BOT_FRONT_SCALAR_MAX,
                Const.BOT_FRONT_RADIUS_MIN, Const.BOT_FRONT_RADIUS_MAX, Const.BOT_FRONT_MIN_DISTANCE)
    }

    private fun getBotBack(rgbaFrame: Mat): List<Circle> {
        return detectCircles("back.jpg", rgbaFrame, Const.BOT_BACK_SCALAR_MIN, Const.BOT_BACK_SCALAR_MAX,
                Const.BOT_BACK_RADIUS_MIN, Const.BOT_BACK_RADIUS_MAX, Const.BOT_BACK_MIN_DISTANCE)
    }

    private fun detectCircles(name: String, frame: Mat, minRange: Scalar, maxRange: Scalar, minRadius: Int, maxRadius: Int, minDistance: Int): List<Circle> {
        val circles = ArrayList<Circle>()
        try {
            val frameProc = Mat()
            Imgproc.medianBlur(frame, frameProc, 3)
            Imgproc.cvtColor(frameProc, frameProc, Imgproc.COLOR_BGR2HSV)
            Core.inRange(frameProc, minRange, maxRange, frameProc)
            Imgproc.GaussianBlur(frameProc, frameProc, Size(9.0, 9.0), 2.0, 2.0)
            val dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, Size(24.0, 24.0))
            val erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, Size(12.0, 12.0))
            Imgproc.erode(frameProc, frameProc, erodeElement)
            Imgproc.dilate(frameProc, frameProc, dilateElement)
            val circlesMat = Mat()
            Imgproc.HoughCircles(frameProc, circlesMat, Imgproc.CV_HOUGH_GRADIENT,
                    1.0, minDistance.toDouble(), 20.0, 20.0, minRadius, maxRadius)

            for (i in 0 until circlesMat.cols()) {
                val circle = circlesMat.get(0, i)
                val center = Point(circle[0].toFloat(), circle[1].toFloat())
                val radius = circle[2].toInt()
                //System.out.println("Ball detected with radius: " + radius + ", and center at " + center);
                circles.add(Circle(center, radius))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return circles
    }

    @Throws(IOException::class)
    fun mat2BufferedImage(matrix: Mat): BufferedImage {
        val mob = MatOfByte()
        Imgcodecs.imencode(".jpg", matrix, mob)
        val ba = mob.toArray()

        return ImageIO.read(ByteArrayInputStream(ba))
    }
}