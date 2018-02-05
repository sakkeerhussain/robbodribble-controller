package main.utils

import main.geometry.Point

data class Path(val vertices: ArrayList<PathVertex>) {
    constructor() : this(ArrayList())

    fun size(): Int {
        return vertices.size
    }

    fun get(i: Int): PathVertex {
        return vertices[i]
    }
}

data class PathVertex(val point: Point, val front: Boolean)