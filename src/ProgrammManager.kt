import devices.Lamp
import devices.Sensor
import devices.SensorType
import devices.WaterDispenser

class ProgrammManager {
    val deskLamp = Lamp(name = "Desktop Lamp", isOn = false)
    val earthHumiditySensor = Sensor(name = "Earth-Humidity Sensor", sensorType = SensorType.EARTH_HUMIDITY)
    val plantWaterDispenser = WaterDispenser(name = "Plant Water Dispenser")
    private val iotDevices = listOf(deskLamp, earthHumiditySensor, plantWaterDispenser)
    fun runSimulation(){
        repeat(10){
            val sensors: List<Sensor> = iotDevices.filterIsInstance<Sensor>()
            sensors.forEach { it.updateData() }
            displayData(sensors)
        }
    }

    private fun displayData(sensors : List<Sensor>){
        sensors.sortedBy { it.sensorType }.forEach {
            println("${it.name}: ${it.readData()}")
        }
    }
}