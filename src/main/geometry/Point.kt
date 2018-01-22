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

    fun getPointAtAngle(angle: Double, distance: Float, high: Boolean): Point {
        val xDelta = distance * Math.cos(angle).toFloat()
        val yDelta = distance * Math.sin(angle).toFloat()
        return if (high)
            Point(this.x + xDelta, this.y + yDelta)
        else
            Point(this.x - xDelta, this.y - yDelta)
    }
}