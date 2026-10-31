package devices

import IoTDevice
import functionalities.Measurable
import java.util.UUID

data class Sensor (override val name : String, override val id : UUID = UUID.randomUUID(), val sensorType: SensorType): Measurable, IoTDevice(name, id) {
    private var data : Double = 0.0
    override fun readData(): Double {
        return data
    }

    override fun updateData() {
        val defaultValue = (sensorType.defaultValue * 100).toInt()
        val offset = (sensorType.maxOffset * 100).toInt()
        val possibleValues = (defaultValue - offset)..(defaultValue + offset)
        val targetValue = possibleValues.random() / 100.0

        data += (targetValue - data) * 0.2
    }

    override fun influenceData(newData : Double) {
        data = newData
    }

}

enum class SensorType(val defaultValue: Double, val maxOffset: Float) {
    AIR_HUMIDITY(50.0, 10.0f),
    EARTH_HUMIDITY(60.0, 20.0f),
    AIR_TEMPERATURE(21.0, 6.0f),
    WATER_TEMPERATURE(20.0, 5.0f),
    BRIGHTNESS(500.0, 90.0f)
}