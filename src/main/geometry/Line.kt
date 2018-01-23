package main.geometry

class Line(val p1:Point, val p2: Point){
    fun mid(): Point {
        return Point((p1.x+p2.x)/2,(p1.y+p2.y)/2)
    }

    fun angleInDegree(): Double {
        return Math.toDegrees(this.angle())
    }

    private fun angle(): Double {
        return if (this.p1. x == this.p2.x)
            1.5708
        else
            Math.atan2((this.p2.y - this.p1.y).toDouble(), (this.p2.x - this.p1.x).toDouble())
    }

    fun length(): Double {
        return p1.distanceTo(p2)
    }

    fun angleBetween(that: Line): Double {
        return that.angleInDegree() - this.angleInDegree()
    }
}