package main.opencv

import main.opencv.models.BoardReference
import main.sensor.SensorsManager
import main.utils.ImageToRealMapper
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.videoio.VideoCapture
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.core.CvType
import org.opencv.core.MatOfPoint


object OpenCV {
    private const val TAG = "OpenCV"

    private var cameraForBotLocation: VideoCapture? = null
    private var cameraForCaliberation: VideoCapture? = null
    private var cameraForBalls: VideoCapture? = null
    //private var mFrame: Mat? = null

    val boardReference : BoardReference = OpenCvUtils.retrieveRefPointsFromFile() ?: BoardReference()

    init {
        ImageToRealMapper.updateMappingConstants()
    }

    fun init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        cameraForBotLocation = VideoCapture()
        cameraForCaliberation = VideoCapture()
        cameraForBalls = VideoCapture()

        val sensor = SensorsManager.getSensorsList()[0]
        setCamUrl(sensor.getImageUrl())

        /*Executors.newCachedThreadPool().submit {
            var lastResult = true
            while (true) {
                if (!lastResult) {
                    Log.d(TAG, "Grab frame error!")
                    Thread.sleep(1000)
                }
                lastResult = grabFrame()
            }
        }*/
    }

    fun setCamUrl(camUrl: String) {
        cameraForBotLocation!!.open(camUrl)
        cameraForCaliberation!!.open(camUrl)
        cameraForBalls!!.open(camUrl)
    }

    fun setCamIndex(index: Int) {
        cameraForBotLocation!!.open(index)
    }

    fun getFrameForBotLocation(): Mat? {
        if (cameraForBotLocation!!.isOpened) {
            try {
                val frame = Mat()
                cameraForBotLocation!!.read(frame)
                return frame
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun getFrameForBalls(): Mat? {
        if (cameraForBalls!!.isOpened) {
            try {
                val frame = Mat()
                cameraForBalls!!.read(frame)
                return frame
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun getFrameForCaliberation(): Mat? {
        if (cameraForCaliberation!!.isOpened) {
            try {
                val frame = Mat()
                cameraForCaliberation!!.read(frame)
                return frame
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    /*fun getFrame(): Mat? {
        if (mFrame == null)
            Thread.sleep(1000)
        return mFrame
    }*/

    /*private fun grabFrame(): Boolean {
        if (camera!!.isOpened) {
            try {
                val frame = Mat()
                camera!!.read(frame)
                mFrame = frame
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }*/

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