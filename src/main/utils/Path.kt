package main.utils

import main.geometry.Point

data class Path(var vertices: ArrayList<PathVertex>, var index: Int) {
    constructor() : this(ArrayList(), 0)

    fun size(): Int {
        return vertices.size
    }

    fun get(i: Int): PathVertex {
        return vertices[i]
    }

    fun getActiveVertex(): PathVertex {
        return vertices[index]
    }
}

data class PathVertex(var point: Point, val front: Boolean) {

    constructor(point: Point) : this(point, true)

    var type: String? = null
    var value: Int? = null

    constructor(type: String, value: Int) : this(Point(-2f, -2f)) {
        this.type = type
        this.value = value
    }
}