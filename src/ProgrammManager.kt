import devices.Lamp
import devices.Sensor
import devices.SensorType
import devices.WaterDispenser
import frames.RefreshFrame

class ProgrammManager {
    val actionQueue = Queue()
    val automations = mutableListOf<Automation>()
    val ioManager = IOManager()

    val deskLamp = Lamp(name = "Desktop Lamp", isOn = false)
    val earthHumiditySensor = Sensor(name = "Earth-Humidity Sensor", sensorType = SensorType.EARTH_HUMIDITY)
    val plantWaterDispenser = WaterDispenser(name = "Plant Water Dispenser")
    private val iotDevices = mutableListOf<IoTDevice>()
    private var sensors = mutableListOf<Sensor>()
    init{
        updateDevices(listOf(deskLamp, earthHumiditySensor, plantWaterDispenser), emptyList())
    }
    fun runSimulation(){
        var startTime = System.currentTimeMillis()

        while(true) { //the end is never!
            val aktuelleZeit = System.currentTimeMillis()
            val intervall = 1000L
            if (aktuelleZeit - startTime >= intervall) {
                actionQueue.addImportant(RefreshFrame(sensors))
                ioManager.displayData(sensors)
                startTime = System.currentTimeMillis()
                checkAllAutomations(actionQueue)
            }else {
                val userAction = ioManager.userAction(iotDevices)
                if (userAction != null) actionQueue.addImportant(userAction)
            }

            val nextAction = actionQueue.remove()?: continue
            nextAction.execute()
        }
    }

    fun checkAllAutomations(actionQueue: Queue){
        automations.forEach{
            val action = it.checkAutomation()?:return@forEach
            if (actionQueue.contains(action)) return@forEach
            actionQueue.add(action)
        }
    }

    fun updateDevices(addedSensors : List<IoTDevice>, deletedSensors : List<IoTDevice>){
        deletedSensors.forEach { iotDevices.add(it) }
        addedSensors.forEach { iotDevices.remove(it) }
        sensors = iotDevices.filterIsInstance<Sensor>().toMutableList()
    }
}