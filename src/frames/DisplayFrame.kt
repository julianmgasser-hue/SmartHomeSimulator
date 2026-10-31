package frames

import devices.Lamp
import devices.Sensor

class DisplayFrame(val sensors : List<Sensor>? = null, val lamps : List<Lamp>? = null): Frame() {
    override fun execute() {
        if (sensors == null && lamps == null) return
        if (sensors != null){
            println("Name | Data | UUID")
            sensors.forEach {sensor ->
                println("${sensor.name} | ${sensor.readData()} | ${sensor.id}")
            }
        }

    }

}