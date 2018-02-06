package main.geometry

class Point(val x: Float, val y: Float) {
    fun distanceTo(that: Point): Double {
        val dx = this.x - that.x
        val dy = this.y - that.y
        return Math.sqrt((dx * dx + dy * dy).toDouble())
    }

    fun isOnLine(line: Line, width: Int): Boolean {
        val lengthSum = Line(line.p1, this).length() + Line(line.p2, this).length()
        return lengthSum - width <= line.length()
    }

    fun isAt(point: Point, width: Int): Boolean {
        return Line(point, this).length() < width
    }

    override fun toString(): String {
        return "($x,$y)"
    }

    fun getPointAtAngleFarFrom(angle: Double, distance: Float, farPoint: Point): Point {
        val xDelta = distance * Math.cos(angle).toFloat()
        val yDelta = distance * Math.sin(angle).toFloat()
        val p1 = Point(this.x + xDelta, this.y + yDelta)
        val p2 = Point(this.x - xDelta, this.y - yDelta)
        return if (Line(p1, farPoint).length() > Line(p2, farPoint).length()) p1 else p2
    }

    fun cvPoint(): org.opencv.core.Point {
        return org.opencv.core.Point(x.toDouble(), y.toDouble())
    }
}