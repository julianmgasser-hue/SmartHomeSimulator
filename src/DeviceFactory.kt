import devices.Lamp
import devices.Sensor
import devices.SensorType
import devices.WaterDispenser

/**
 * Simple Factory: zentralisiert die Erstellung unterschiedlicher IoT-Geraete.
 * KI-unterstuetzter Code; siehe KI_Nutzung.md im Projektverzeichnis.
 */
class DeviceFactory {
    fun createDevice(
        type: DeviceType,
        name: String,
        sensorType: SensorType = SensorType.EARTH_HUMIDITY
    ): IoTDevice {
        require(name.isNotBlank()) { "Der Geraetename darf nicht leer sein." }

        return when (type) {
            DeviceType.LAMP -> Lamp(name = name, isOn = false)
            DeviceType.SENSOR -> Sensor(name = name, sensorType = sensorType)
            DeviceType.WATER_DISPENSER -> WaterDispenser(name = name)
        }
    }
}

enum class DeviceType {
    LAMP,
    SENSOR,
    WATER_DISPENSER
}
