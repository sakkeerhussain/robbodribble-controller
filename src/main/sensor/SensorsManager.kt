package main.sensor

import main.controllers.Const

class SensorsManager{
    private val sensorsList:ArrayList<Sensor> by lazy { DataInitializer.sensors }
    companion object {
        private var instance: SensorsManager? = null

        fun get(): SensorsManager {
            if (instance == null)
                instance = SensorsManager()
            return instance!!
        }
    }

    fun getSensorsList(): List<Sensor> {
        return sensorsList
    }

    private object DataInitializer {
        var sensors = ArrayList<Sensor>()

        init {
//            sensors.add(Sensor("10.7.120.15", "8080"))
//              sensors.add(Sensor("10.7.120.3", false, false))
//              sensors.add(Sensor("127.0.0.1", "9000", false, false))
//            sensors.add(Sensor(Const.IP_NEXUS, "8080", false, false))
//            sensors.add(Sensor("10.7.120.22", "8080", false, false))
            sensors.add(Sensor(Const.IP_SAKKEER, "8080"))
        }
    }
}
data class Sensor(val ip: String, val port: String) {
    fun getImageUrl(): String {
        return "http://$ip:$port/image/"
    }
}