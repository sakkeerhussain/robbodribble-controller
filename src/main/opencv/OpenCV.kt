package main.opencv

import main.opencv.models.ReferencePoint
import main.utils.ImageToRealMapper
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

    var refPoint1: ReferencePoint = ReferencePoint(0.01f, 0.01f, -10f, -10f)
    var refPointMid12: ReferencePoint = ReferencePoint(622.5f, 12.5f, 140f, -10f)
    var refPoint2: ReferencePoint = ReferencePoint(1245f, 25f, 290f, -10f)
    var refPoint3: ReferencePoint = ReferencePoint(0.01f, 865f, -10f, 190f)
    var refPointMid34: ReferencePoint = ReferencePoint(622.5f, 852.5f, 140f, 190f)
    var refPoint4: ReferencePoint = ReferencePoint(1245f, 840f, 290f, 190f)

    init {
        ImageToRealMapper.updateMappingConstants()
    }

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