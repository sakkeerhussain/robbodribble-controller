package main.opencv

import main.opencv.models.ReferencePoint
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

    var refPointOB1: ReferencePoint = ReferencePoint(50f, 32f, -10f, -10f)
    var refPointOB2: ReferencePoint = ReferencePoint(1290f, 20f, 290f, -10f)
    var refPointOB3: ReferencePoint = ReferencePoint(67f, 867f, -10f, 190f)
    var refPointOB4: ReferencePoint = ReferencePoint(1305f, 840f, 290f, 190f)

    var refPoint1: ReferencePoint = ReferencePoint(80f, 62f, 0.01f, 0.01f)
    var refPointMid12: ReferencePoint = ReferencePoint(622.5f, 60f, 140f, 0.01f)
    var refPoint2: ReferencePoint = ReferencePoint(1260f, 50f, 280f, 0.01f)
    var refPoint3: ReferencePoint = ReferencePoint(97f, 837f, 0.01f, 180f)
    var refPointMid34: ReferencePoint = ReferencePoint(622.5f, 825f, 140f, 180f)
    var refPoint4: ReferencePoint = ReferencePoint(1275f, 810f, 280f, 180f)

    var refPointC: ReferencePoint = ReferencePoint(676f, 440f, 140f, 90f)
    var refPointQ1: ReferencePoint = ReferencePoint(392f, 446f, 70f, 90f)
    var refPointQ2: ReferencePoint = ReferencePoint(958f, 435.5f, 210f, 90f)

    init {
        ImageToRealMapper.updateMappingConstants()
    }

    fun init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        camera = VideoCapture()

        Executors.newCachedThreadPool().submit {
            var lastResult = true
            while (true) {
                if (!lastResult) {
                    Log.d(TAG, "Grab frame error!")
                    Thread.sleep(1000)
                }
                lastResult = grabFrame()
            }
        }
    }

    fun setCamUrl(camUrl: String) {
        camera!!.open(camUrl)
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
        val points = arrayOf(refPoint1.pointImage.cvPoint(), refPointMid12.pointImage.cvPoint(), refPoint2.pointImage.cvPoint(),
                refPoint4.pointImage.cvPoint(), refPointMid34.pointImage.cvPoint(), refPoint3.pointImage.cvPoint())
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
        val points = arrayOf(refPointOB1.pointImage.cvPoint(), refPointOB2.pointImage.cvPoint(),
                refPointOB4.pointImage.cvPoint(), refPointOB3.pointImage.cvPoint())
        val matOfPoint = MatOfPoint()
        matOfPoint.fromArray(*points)
        return matOfPoint
    }
}