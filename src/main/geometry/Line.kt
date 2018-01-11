package main.geometry

class Line(val p1:Point, val p2: Point){
    fun mid(): Point {
        return Point((p1.x+p2.x)/2,(p1.y+p2.y)/2)
    }

    fun angle(): Double {
        return if (this.p1. x == this.p2.x)
            90.0
        else
            Math.atan(((this.p2.y - this.p1.y) / (this.p2.x - this.p1.x)).toDouble())
    }

    fun length(): Double {
        return p1.distanceTo(p2)
    }

    fun angleBetween(that: Line): Double {
        return that.angle() - this.angle()
    }
}