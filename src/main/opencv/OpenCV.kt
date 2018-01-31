package main.opencv

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.videoio.VideoCapture

object OpenCV {

    private var camera: VideoCapture? = null
    private var imagePath: String? = null

    internal fun init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        camera = VideoCapture()
    }

    internal fun setCamUrl(camUrl: String) {
        camera!!.open(camUrl)
    }

    internal fun setCamIndex(index: Int) {
        camera!!.open(index)
    }

    internal fun setImagePath(imagePath: String) {
        OpenCV.imagePath = imagePath
    }

    internal fun grabFrame(): Mat? {
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
}