package main.utils

import main.geometry.Point

data class Path(var vertices: ArrayList<PathVertex>) {
    constructor() : this(ArrayList())

    fun size(): Int {
        return vertices.size
    }

    fun get(i: Int): PathVertex {
        return vertices[i]
    }
}

data class PathVertex(var point: Point, val front: Boolean) {

    constructor(point: Point) : this(point, true)
}