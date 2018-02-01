package main.opencv

import main.opencv.models.ReferencePoint
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.videoio.VideoCapture
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.core.CvType
import org.opencv.core.MatOfPoint


object OpenCV {

    private var camera: VideoCapture? = null
    private var imagePath: String? = null

    private val refPoint1: ReferencePoint? = null
    private val refPoint2: ReferencePoint? = null
    private val refPoint3: ReferencePoint? = null
    private val refPoint4: ReferencePoint? = null

    fun init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        camera = VideoCapture()
    }

    fun setCamUrl(camUrl: String) {
        camera!!.open(camUrl)
    }

    fun setCamIndex(index: Int) {
        camera!!.open(index)
    }

    fun setImagePath(imagePath: String) {
        OpenCV.imagePath = imagePath
    }

    fun getFrame(): Mat? {
        if (camera!!.isOpened) {
            try {
                val frame = Mat()
                camera!!.read(frame)
                return frame
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        } else if (imagePath != null) {
            return Imgcodecs.imread(imagePath)
        }
        return null
    }

    //Functions
    fun clipFrame(frame: Mat): Mat {
        val refPoints = getRefPoints() ?: return frame
        val frameRes = Mat()
        val mask = Mat.zeros(frame.rows(), frame.cols(), CvType.CV_8UC4)
        Imgproc.fillConvexPoly(mask, refPoints, Scalar(255.0, 255.0, 255.0))
        frame.copyTo(frameRes, mask)
        return frameRes
    }

    private fun getRefPoints(): MatOfPoint? {
        if (refPoint1 == null || refPoint2 == null || refPoint3 == null || refPoint4 == null) {
            return null
        }
        val points = arrayOf(refPoint1.pointImage.cvPoint(), refPoint2.pointImage.cvPoint(), refPoint4.pointImage.cvPoint(), refPoint3.pointImage.cvPoint())
        val matOfPoint = MatOfPoint()
        matOfPoint.fromArray(*points)
        return matOfPoint
    }
}