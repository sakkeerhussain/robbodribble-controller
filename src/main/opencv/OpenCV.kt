package main.opencv

import main.opencv.models.ReferencePoint
import main.utils.ImageToRealMapper
import main.utils.Log
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
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

    var refPoint1: ReferencePoint = ReferencePoint(30f, 1f, -10f, -10f)
    var refPointMid12: ReferencePoint = ReferencePoint(622.5f, 5f, 140f, -10f)
    var refPoint2: ReferencePoint = ReferencePoint(1280f, 5f, 290f, -10f)
    var refPoint3: ReferencePoint = ReferencePoint(25f, 865f, -10f, 190f)
    var refPointMid34: ReferencePoint = ReferencePoint(622.5f, 870f, 140f, 190f)
    var refPoint4: ReferencePoint = ReferencePoint(1280f, 870f, 290f, 190f)

    var refPointC: ReferencePoint = ReferencePoint(663f, 438f, 140f, 90f)
    var refPointQ1: ReferencePoint = ReferencePoint(377f, 438f, 70f, 90f)
    var refPointQ2: ReferencePoint = ReferencePoint(945f, 432.5f, 210f, 90f)

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
}