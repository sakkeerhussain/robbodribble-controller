package main.controllers

class SensorsManager{
    companion object {
        val SENSORS_LIST:ArrayList<Sensor> by lazy { DataInitializer.sensors }
        private var instance: SensorsManager? = null

        fun get(): SensorsManager {
            if (instance == null)
                instance = SensorsManager()
            return instance!!
        }
    }

    private object DataInitializer {
        var sensors = ArrayList<Sensor>();

        init {
//            sensors.add(Sensor("192.168.1.50"))
//            sensors.add(Sensor("10.7.170.6"))
            sensors.add(Sensor(Const.IP_NEXUS))
//            sensors.add(Sensor(Const.IP_SAKKEER))
        }
    }
}
data class Sensor(val ip: String){}