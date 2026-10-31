import devices.Lamp
import devices.Sensor
import devices.SensorType
import devices.WaterDispenser
import frames.RefreshFrame

class ProgrammManager {
    val actionQueue = FrameQueue()
    val userQueue = FrameQueue()
    val automations = mutableListOf<Automation>()
    val ioManager = IOManager()

    val deskLamp = Lamp(name = "Desktop Lamp", isOn = false)
    val earthHumiditySensor = Sensor(name = "Earth-Humidity Sensor", sensorType = SensorType.EARTH_HUMIDITY)
    val plantWaterDispenser = WaterDispenser(name = "Plant Water Dispenser")
    private val iotDevices = mutableListOf<IoTDevice>()
    private val sensors = mutableListOf<Sensor>()
    init{
        updateDevices(listOf(deskLamp, earthHumiditySensor, plantWaterDispenser), emptyList())
    }
    fun runSimulation(){
        val startTime = System.currentTimeMillis()

        while(true) { //the end is never!
            val aktuelleZeit = System.currentTimeMillis()
            val intervall = 1000L
            if (aktuelleZeit - startTime >= intervall) {
                actionQueue.addImportant(RefreshFrame(sensors))
                ioManager.displayData(sensors)
            }else {
                checkAllAutomations()
                val userAction = ioManager.userAction(iotDevices)
                if (userAction != null) actionQueue.addImportant(userAction)
            }

            actionQueue.remove().execute()
        }

    }

    fun checkAllAutomations(){
        automations.forEach{
            actionQueue.add(it.checkAutomation()?:return@forEach)
        }
    }

    fun updateDevices(addedSensors : List<IoTDevice>, deletedSensors : List<IoTDevice>){
        deletedSensors.forEach { iotDevices.add(it) }
        addedSensors.forEach { iotDevices.add(it) }
    }
}