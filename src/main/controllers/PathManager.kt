package main.controllers

import main.utils.Path


object PathManager {
    private var listeners = ArrayList<Listener>()
    fun addListener(listener: Listener) {
        if (listener !in listeners)
            listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        if (path != null)
            listeners.forEach { it.pathChanged(path!!) }
    }

    private var path: Path? = null
    fun getPath(): Path? {
        return path
    }

    fun updatePath(path: Path?) {
        this.path = path
        notifyListeners()
    }

    interface Listener {
        fun pathChanged(path: Path)
    }
}