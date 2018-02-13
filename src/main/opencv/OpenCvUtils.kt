package main.opencv

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import main.controllers.Ball
import main.controllers.BotLocation
import main.geometry.Circle
import main.geometry.Line
import main.geometry.Point
import main.opencv.models.BoardReference
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
import java.io.File

object OpenCvUtils {
    private const val TAG = "OpenCvUtils"

    fun getBallsOnBoard(): List<Ball>? {
        val startTime = Date().time
        val circles = getBalls() ?: return null
        val balls = circles.mapTo(ArrayList()) { Ball(convertPointToBoard(it.center)) }
        Log.d("Ball detection", "${balls.size} balls detect with: "
                + (Date().time - startTime) + "ms")
        return balls
    }

    fun getBotLocationOnBoard(): BotLocation? {
        val location = getBotLocation() ?: return null
        return BotLocation(location.angle,
                convertPointToBoard(location.backLeft),
                convertPointToBoard(location.backRight),
                convertPointToBoard(location.frontLeft),
                convertPointToBoard(location.frontRight))
    }

    private fun getBalls(): List<Circle>? {
        var frame = OpenCV.getFrame()
        if (frame == null || frame.rows() == 0 || frame.cols() == 0) {
            Log.d(TAG, "No frame received for balls")
            return null
        }
        frame = OpenCV.clipFrame(frame)
        val circles = detectCircles(frame, Const.BALL_SCALAR_MIN, Const.BALL_SCALAR_MAX,
                Const.BALL_RADIUS_MIN, Const.BALL_RADIUS_MAX, Const.BALL_MIN_DISTANCE)
        frame.release()
        return circles
    }

    private fun getBotLocation(): BotLocation? {
        val startTime = Date().time
        var frame = OpenCV.getFrame()
        if (frame == null || frame.rows() == 0 || frame.cols() == 0) {
            Log.d(TAG, "No frame received for balls")
            return null
        }
        frame = OpenCV.clipFrameForBotLocation(frame)
        val frontCircles = getBotFront(frame)
        val backCircles = getBotBack(frame)
        frame.release()
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

    fun drawBotToFrame(frame: Mat) {
        val botLocation = OpenCvUtils.getBotLocation() ?: return
        val botFrontDrawColor = Scalar(0.0, 0.0, 0.0)
        val botDrawColor = Scalar(255.0, 0.0, 0.0)

        Imgproc.line(frame, botLocation.frontLeft.cvPoint(), botLocation.frontRight.cvPoint(), botFrontDrawColor, 3)
        Imgproc.line(frame, botLocation.frontLeft.cvPoint(), botLocation.backLeft.cvPoint(), botDrawColor, 3)
        Imgproc.line(frame, botLocation.frontRight.cvPoint(), botLocation.backRight.cvPoint(), botDrawColor, 3)
        Imgproc.line(frame, botLocation.backLeft.cvPoint(), botLocation.backRight.cvPoint(), botDrawColor, 3)
    }

    fun drawBallsToFrame(frame: Mat) {
        val balls = OpenCvUtils.getBalls() ?: return
        val ballDrawColor = Scalar(0.0, 255.0, 255.0)
        for (ball in balls) {
            Imgproc.circle(frame, ball.center.cvPoint(), ball.radius, ballDrawColor, 3)
        }
    }

    fun drawBordToFrame(frame: Mat) {
        val boardOutDrawColor = Scalar(0.0, 0.0, 255.0)
        val boardDrawColor = Scalar(0.0, 10.0, 200.0)
        val refPoint1 = OpenCV.boardReference.refPoint1.pointImage.cvPoint()
        val refPointMid12 = OpenCV.boardReference.refPointMid12.pointImage.cvPoint()
        val refPoint2 = OpenCV.boardReference.refPoint2.pointImage.cvPoint()
        val refPoint3 = OpenCV.boardReference.refPoint3.pointImage.cvPoint()
        val refPointMid34 = OpenCV.boardReference.refPointMid34.pointImage.cvPoint()
        val refPoint4 = OpenCV.boardReference.refPoint4.pointImage.cvPoint()
        val refPointC = OpenCV.boardReference.refPointC.pointImage.cvPoint()
        val refPointQ1 = OpenCV.boardReference.refPointQ1.pointImage.cvPoint()
        val refPointQ2 = OpenCV.boardReference.refPointQ2.pointImage.cvPoint()

        val refPointO1 = OpenCV.boardReference.refPointOB1.pointImage.cvPoint()
        val refPointO2 = OpenCV.boardReference.refPointOB2.pointImage.cvPoint()
        val refPointO3 = OpenCV.boardReference.refPointOB3.pointImage.cvPoint()
        val refPointO4 = OpenCV.boardReference.refPointOB4.pointImage.cvPoint()

        Imgproc.line(frame, refPoint1, refPointMid12, boardDrawColor, 3)
        Imgproc.line(frame, refPointMid12, refPoint2, boardDrawColor, 3)
        Imgproc.line(frame, refPoint1, refPoint3, boardDrawColor, 3)
        Imgproc.line(frame, refPoint4, refPoint2, boardDrawColor, 3)
        Imgproc.line(frame, refPoint3, refPointMid34, boardDrawColor, 3)
        Imgproc.line(frame, refPointMid34, refPoint4, boardDrawColor, 3)

        Imgproc.line(frame, refPointO1, refPointO2, boardOutDrawColor, 3)
        Imgproc.line(frame, refPointO2, refPointO4, boardOutDrawColor, 3)
        Imgproc.line(frame, refPointO4, refPointO3, boardOutDrawColor, 3)
        Imgproc.line(frame, refPointO3, refPointO1, boardOutDrawColor, 3)

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

    fun saveRefPointsToFile() {
        val file = File(Const.FileName.REF_POINT)
        if (!file.exists()) {
            File(file.parent).mkdir()
        }
        val boardReferenceJson = GsonBuilder().create().toJson(OpenCV.boardReference)
        file.writeText(boardReferenceJson)
    }

    fun retrieveRefPointsFromFile() : BoardReference? {
        val file = File(Const.FileName.REF_POINT)
        if (!file.exists()) {
            Log.d(TAG, "${file.name} not found")
            return null
        }
        Log.d(TAG, "Loading reference point configuaration from ${file.name}")
        val boardReferenceJson = file.readText()
        val gson = GsonBuilder().create()
        return gson.fromJson(boardReferenceJson, BoardReference::class.java)
    }
}