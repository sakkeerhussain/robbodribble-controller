package main.geometry

class Line(val p1:Point, val p2: Point){
    fun mid(): Point {
        return Point((p1.x+p2.x)/2,(p1.y+p2.y)/2)
    }
}