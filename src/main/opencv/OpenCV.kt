package main.opencv

import main.opencv.models.BoardReference
import main.sensor.SensorsManager
import main.utils.ImageToRealMapper
import main.utils.Log
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.videoio.VideoCapture
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.core.CvType
import org.opencv.core.MatOfPoint
import java.util.concurrent.Executors


object OpenCV {
    private const val TAG = "OpenCV"

    private var camera: VideoCapture? = null
    private var mFrame: Mat? = null
    private var mCameraUrl: String? = null
    private var mRunFrameGrabber = false

    val boardReference: BoardReference = OpenCvUtils.retrieveRefPointsFromFile() ?: BoardReference()

    init {
        ImageToRealMapper.updateMappingConstants()
    }

    fun init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        camera = VideoCapture()

        val sensor = SensorsManager.getSensorsList()[0]
        setCameraUrl(sensor.getImageUrl())
        startFrameGrabber()
    }

    fun stopFrameGrabber() {
        if (mRunFrameGrabber) {
            println("Frame grabbing stopping...")
            mRunFrameGrabber = false
            return
        }
        println("Frame is not running already")
    }

    fun startFrameGrabber() {
        if (mRunFrameGrabber) {
            Log.d(TAG, "Frame grabber already running")
            return
        }
        Executors.newCachedThreadPool().submit {
            var lastResult = true
            mRunFrameGrabber = true
            while (true) {
                if (!mRunFrameGrabber) {
                    Log.d(TAG, "Frame grabbing stopped")
                    break
                }
                if (!lastResult) {
                    Log.d(TAG, "Grab frame error!")
                    Thread.sleep(1000)
                }
                Log.d(TAG, "Grabbing frame")
                lastResult = grabFrame()
            }
        }
    }

    private fun setCameraUrl(cameraUrl: String) {
        this.mCameraUrl = cameraUrl
    }

    fun setCamIndex(index: Int) {
        camera!!.open(index)
    }

    fun getFrame(): Mat? {
        if (mFrame == null)
            Thread.sleep(1000)
        return mFrame
    }

    private fun grabFrame(): Boolean {
        try {
            if (mCameraUrl == null) {
                return false
            }
            camera!!.open(mCameraUrl)
            if (camera!!.isOpened) {
                val frame = Mat()
                camera!!.read(frame)
                mFrame = frame
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    //Functions
    fun clipFrame(frame: Mat): Mat {
        val refPoints = getRefPoints() ?: return frame
        val frameRes = Mat()
        val mask = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_8UC3)
        Imgproc.fillConvexPoly(mask, refPoints, Scalar(255.0, 255.0, 255.0))
        frame.copyTo(frameRes, mask)
        return frameRes
    }

    private fun getRefPoints(): MatOfPoint? {
        val points = arrayOf(boardReference.refPoint1.pointImage.cvPoint(), boardReference.refPointMid12.pointImage.cvPoint(),
                boardReference.refPoint2.pointImage.cvPoint(), boardReference.refPoint4.pointImage.cvPoint(),
                boardReference.refPointMid34.pointImage.cvPoint(), boardReference.refPoint3.pointImage.cvPoint())
        val matOfPoint = MatOfPoint()
        matOfPoint.fromArray(*points)
        return matOfPoint
    }

    fun clipFrameForBotLocation(frame: Mat): Mat {
        val refPoints = getRefPointsForBotLocation() ?: return frame
        val frameRes = Mat()
        val mask = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_8UC3)
        Imgproc.fillConvexPoly(mask, refPoints, Scalar(255.0, 255.0, 255.0))
        frame.copyTo(frameRes, mask)
        return frameRes
    }

    private fun getRefPointsForBotLocation(): MatOfPoint? {
        val points = arrayOf(boardReference.refPointOB1.pointImage.cvPoint(), boardReference.refPointOB2.pointImage.cvPoint(),
                boardReference.refPointOB4.pointImage.cvPoint(), boardReference.refPointOB3.pointImage.cvPoint())
        val matOfPoint = MatOfPoint()
        matOfPoint.fromArray(*points)
        return matOfPoint
    }
}