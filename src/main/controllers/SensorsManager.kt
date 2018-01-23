package main.controllers

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

    fun getBallsSensorsList(): List<Sensor> {
        return sensorsList.filter { !it.botOnly }
    }

    fun getBotSensorsList(): List<Sensor> {
        return sensorsList.filter { !it.ballOnly }
    }

    private object DataInitializer {
        var sensors = ArrayList<Sensor>()

        init {
//            sensors.add(Sensor("192.168.1.50"))
//              sensors.add(Sensor("10.7.120.3", false, false))
//              sensors.add(Sensor("127.0.0.1", "9000", false, false))
//            sensors.add(Sensor(Const.IP_NEXUS, "8080", false, false))
//            sensors.add(Sensor("10.7.120.22", "8080", false, false))
            sensors.add(Sensor(Const.IP_SAKKEER, "8080", false, false))
        }
    }
}
data class Sensor(val ip: String, val port: String, val ballOnly: Boolean, val botOnly: Boolean){}