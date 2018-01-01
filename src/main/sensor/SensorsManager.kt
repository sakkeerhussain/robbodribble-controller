package main.sensor

class SensorsManager{
    companion object {
        public val SENSORS_LIST:ArrayList<Sensor> by lazy { DataInitializer.sensors }
    }

    private object DataInitializer {
        var sensors = ArrayList<Sensor>();

        init {
            sensors.add(Sensor("192.168.1.50"))
        }
    }

}
data class Sensor(val ip: String){}