package devices

import IoTDevice
import functionalities.LiquidDispensable
import java.util.UUID

class WaterDispenser(override val name: String, override val id: UUID = UUID.randomUUID()) : LiquidDispensable, IoTDevice(name, id) {
    override fun dispense(influencedDevices: List<Sensor>) {
        influencedDevices.filter {
            it.sensorType == SensorType.EARTH_HUMIDITY
        }.forEach {
            val lastState = it.readData()
            it.influenceData(lastState + 10.0)
        }
    }
}