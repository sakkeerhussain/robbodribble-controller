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
import java.util.*
import java.util.concurrent.Executors


object OpenCV {
    private const val TAG = "OpenCV"

    private var camera: VideoCapture? = null
    private var mFrame: Mat? = null
    private var mCameraUrl: String? = null
    private var mFrameGrabberRunning = false

    val boardReference: BoardReference = OpenCvUtils.retrieveRefPointsFromFile() ?: BoardReference()

    init {
        ImageToRealMapper.updateMappingConstants()
    }

    fun init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        mFrame = Mat()
        camera = VideoCapture()

        val sensor = SensorsManager.getSensorsList()[0]
        setCameraUrl(sensor.getImageUrl())
        //startFrameGrabber()
    }

    private fun stopFrameGrabber() {
        if (mFrameGrabberRunning) {
            Log.d(TAG, "Frame grabbing stopping...")
            mFrameGrabberRunning = false
            return
        }
        Log.d(TAG, "Frame grabber stop failed. grabber is not running already")
    }

    private fun startFrameGrabber() {
        if (mFrameGrabberRunning) {
            Log.d(TAG, "Frame grabber already running")
            return
        }
        mFrameGrabberRunning = true

        //Starting frame grabber
        Log.d(TAG, "Frame grabber Starting...")
        Executors.newCachedThreadPool().submit {
            var lastResult = true
            while (true) {
                if (!mFrameGrabberRunning) {
                    Log.d(TAG, "Frame grabbing stopped")
                    break
                }
                if (!lastResult) {
                    Log.d(TAG, "Grab frame error!")
                    Thread.sleep(1000)
                } else {
                    Thread.sleep(10)
                }
                lastResult = grabFrame()
            }
        }

        //Setting frame grabber stopper
        /*Timer().schedule(object: TimerTask() {
            override fun run() {
                stopFrameGrabber()
            }
        }, 10000)*/
    }

    private fun setCameraUrl(cameraUrl: String) {
        this.mCameraUrl = cameraUrl
    }

    fun setCamIndex(index: Int) {
        camera!!.open(index)
    }

    fun getFrame(): Mat? {
        if (!mFrameGrabberRunning) {
            startFrameGrabber()
        }

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
                camera!!.read(mFrame)
                return true
            }
        } catch (e: Exception) {
            Log.d(TAG, "Exception: ${e.localizedMessage}")
        } catch (e: Error) {
            Log.d(TAG, "Error: ${e.localizedMessage}")
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