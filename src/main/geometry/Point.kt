package main.geometry

class Point(val x:Float, val y: Float){
    fun distanceTo(that: Point): Double {
        val dx = this.x - that.x
        val dy = this.y - that.y
        return Math.sqrt((dx * dx + dy * dy).toDouble())
    }
}