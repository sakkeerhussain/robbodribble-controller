package main.opencv

import main.controllers.Ball
import main.controllers.BotLocation
import main.geometry.Circle
import main.geometry.Line
import main.geometry.Point
import main.sensor.Sensor
import main.utils.ImageToRealMapper.convertPointToBoard
import main.utils.Log
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO
import org.opencv.core.Mat

object OpenCvUtils {
    private const val TAG = "OpenCvUtils"

    fun getBallsOnBoard(sensor: Sensor): List<Ball>? {
        val startTime = Date().time
        val circles = getBalls(sensor) ?: return null
        val balls = circles.mapTo(ArrayList()) { Ball(convertPointToBoard(it.center)) }
        Log.d("Ball detection", "Time taken to detect ball: "
                + (Date().time - startTime) + "ms")
        return balls
    }

    fun getBotLocationOnBoard(sensor: Sensor): BotLocation? {
        val location = getBotLocation(sensor) ?: return null
        return BotLocation(location.angle,
                convertPointToBoard(location.backLeft),
                convertPointToBoard(location.backRight),
                convertPointToBoard(location.frontLeft),
                convertPointToBoard(location.frontRight))
    }

    private fun getBalls(sensor: Sensor): List<Circle>? {
        OpenCV.setCamUrl(sensor.getImageUrl())
        val frame = OpenCV.getFrame()
        if (frame == null || frame.rows() == 0 || frame.cols() == 0) {
            Log.d(TAG, "No frame received")
            return null
        }
        return detectCircles(frame, Const.BALL_SCALAR_MIN, Const.BALL_SCALAR_MAX,
                Const.BALL_RADIUS_MIN, Const.BALL_RADIUS_MAX, Const.BALL_MIN_DISTANCE)
    }

    private fun getBotLocation(sensor: Sensor): BotLocation? {
        val startTime = Date().time
        OpenCV.setCamUrl(sensor.getImageUrl())
        var frame = OpenCV.getFrame()
        if (frame == null || frame.rows() == 0 || frame.cols() == 0) {
            Log.d(TAG, "No frame received")
            return null
        }
        frame = OpenCV.clipFrame(frame)
        val frontCircles = getBotFront(frame)
        val backCircles = getBotBack(frame)
        if (frontCircles.isEmpty() || backCircles.isEmpty()) {
            Log.d(TAG, "Bot not found")
            return null
        } else if (frontCircles.size > 1 || backCircles.size > 1) {
            //TODO("Handle multiple bot detection issue")
        }
        val frontCenter = frontCircles.get(0).center
        val backCenter = backCircles.get(0).center
        val centerLine = Line(frontCenter, backCenter)
        val centerLineAngle = centerLine.angle()
        val centerToCornerLength = (centerLine.length() * Const.BOT_LOCATOR_DISTANCE_RATIO).toFloat()
        val frontLeft = frontCenter.getPointAtAngleFarFrom(centerLineAngle + Const.BOT_LOCATOR_ANGLE_45,
                centerToCornerLength, backCenter)
        val frontRight = frontCenter.getPointAtAngleFarFrom(centerLineAngle - Const.BOT_LOCATOR_ANGLE_45,
                centerToCornerLength, backCenter)
        val backLeft = backCenter.getPointAtAngleFarFrom(centerLineAngle + Const.BOT_LOCATOR_ANGLE_135,
                centerToCornerLength, frontCenter)
        val backRight = backCenter.getPointAtAngleFarFrom(centerLineAngle - Const.BOT_LOCATOR_ANGLE_135,
                centerToCornerLength, frontCenter)
        Log.d("Bot detection", "Time taken to detect bot: " + (Date().getTime() - startTime) + "ms")
        return BotLocation(centerLineAngle, backLeft, backRight, frontLeft, frontRight)
    }

    private fun getBotFront(rgbaFrame: Mat): List<Circle> {
        return detectCircles(rgbaFrame, Const.BOT_FRONT_SCALAR_MIN, Const.BOT_FRONT_SCALAR_MAX,
                Const.BOT_FRONT_RADIUS_MIN, Const.BOT_FRONT_RADIUS_MAX, Const.BOT_FRONT_MIN_DISTANCE)
    }

    private fun getBotBack(rgbaFrame: Mat): List<Circle> {
        return detectCircles(rgbaFrame, Const.BOT_BACK_SCALAR_MIN, Const.BOT_BACK_SCALAR_MAX,
                Const.BOT_BACK_RADIUS_MIN, Const.BOT_BACK_RADIUS_MAX, Const.BOT_BACK_MIN_DISTANCE)
    }

    private fun detectCircles(frame: Mat, minRange: Scalar, maxRange: Scalar, minRadius: Int, maxRadius: Int, minDistance: Int): List<Circle> {
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
            Log.d(TAG, "Exception in circle detection, ${e.message}")
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

    fun drawBordToFrame(frame: Mat) {
        val boardDrawColor = Scalar(0.0, 0.0, 255.0)
        val refPoint1 = OpenCV.refPoint1.pointImage.cvPoint()
        val refPointMid12 = OpenCV.refPointMid12.pointImage.cvPoint()
        val refPoint2 = OpenCV.refPoint2.pointImage.cvPoint()
        val refPoint3 = OpenCV.refPoint3.pointImage.cvPoint()
        val refPointMid34 = OpenCV.refPointMid34.pointImage.cvPoint()
        val refPoint4 = OpenCV.refPoint4.pointImage.cvPoint()
        val refPointC = OpenCV.refPointC.pointImage.cvPoint()
        val refPointQ1 = OpenCV.refPointQ1.pointImage.cvPoint()
        val refPointQ2 = OpenCV.refPointQ2.pointImage.cvPoint()

        Imgproc.line(frame, refPoint1, refPointMid12, boardDrawColor, 3)
        Imgproc.line(frame, refPointMid12, refPoint2, boardDrawColor, 3)
        Imgproc.line(frame, refPoint1, refPoint3, boardDrawColor, 3)
        Imgproc.line(frame, refPoint4, refPoint2, boardDrawColor, 3)
        Imgproc.line(frame, refPoint3, refPointMid34, boardDrawColor, 3)
        Imgproc.line(frame, refPointMid34, refPoint4, boardDrawColor, 3)

        Imgproc.circle(frame, refPointC, 6, boardDrawColor, 3)
        Imgproc.circle(frame, refPointQ1, 6, boardDrawColor, 3)
        Imgproc.circle(frame, refPointQ2, 6, boardDrawColor, 3)

        Imgproc.putText(frame, "1", refPoint1, Core.FONT_HERSHEY_PLAIN, 3.0, boardDrawColor, 3)
        Imgproc.putText(frame, "2", refPoint2, Core.FONT_HERSHEY_PLAIN, 3.0, boardDrawColor, 3)
        Imgproc.putText(frame, "3", refPoint3, Core.FONT_HERSHEY_PLAIN, 3.0, boardDrawColor, 3)
        Imgproc.putText(frame, "4", refPoint4, Core.FONT_HERSHEY_PLAIN, 3.0, boardDrawColor, 3)

        Imgproc.putText(frame, "C", refPointC, Core.FONT_HERSHEY_PLAIN, 3.0, boardDrawColor, 3)
        Imgproc.putText(frame, "Q1", refPointQ1, Core.FONT_HERSHEY_PLAIN, 3.0, boardDrawColor, 3)
        Imgproc.putText(frame, "Q2", refPointQ2, Core.FONT_HERSHEY_PLAIN, 3.0, boardDrawColor, 3)
    }
}