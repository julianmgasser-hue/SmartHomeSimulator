import devices.Sensor
import devices.SensorType
import exceptions.UnrealisticSimulationException
import frames.Frame
import frames.RefreshFrame
import kotlin.system.exitProcess

class ProgrammManager {
    val actionQueue = Queue<Frame>()
    val automations = mutableListOf<Automation>()
    val ioManager = IOManager()
    val deviceFactory = DeviceFactory()

    private val iotDevices = mutableListOf<IoTDevice>()
    private var sensors = mutableListOf<Sensor>()

    init {
        updateDevices(
            listOf(
                deviceFactory.createDevice(DeviceType.LAMP, "Desktop Lamp"),
                deviceFactory.createDevice(DeviceType.SENSOR, "Plant Earth-Humidity Sensor", SensorType.EARTH_HUMIDITY),
                deviceFactory.createDevice(DeviceType.WATER_DISPENSER, "Plant Water Dispenser")
            ), emptyList()
        )
    }

    fun runSimulation() {
        var startTime = System.currentTimeMillis()

        while (true) { //the end is never!
            val aktuelleZeit = System.currentTimeMillis()
            val intervall = 1000L
            if (aktuelleZeit - startTime >= intervall) {
                actionQueue.addImportant(RefreshFrame(sensors))
                ioManager.displayData(sensors)
                startTime = System.currentTimeMillis()
            } else {
                val userAction = ioManager.userAction(iotDevices)
                if (userAction != null) actionQueue.addImportant(userAction)
            }

            val nextAction = actionQueue.remove() ?: continue
            try{
                nextAction.execute()
            }catch(e : UnrealisticSimulationException){
                println("Oh no: $e")
                println("Game Over!")
                exitProcess(0)
            }
            if (nextAction is RefreshFrame) checkAllAutomations(actionQueue)
        }
    }

    fun checkAllAutomations(actionQueue: Queue<Frame>) {
        automations.forEach {
            val action = it.checkAutomation() ?: return@forEach
            if (actionQueue.contains(action)) return@forEach
            actionQueue.add(action)
        }
    }

    fun updateDevices(addedSensors: List<IoTDevice>, deletedSensors: List<IoTDevice>) {
        deletedSensors.forEach { iotDevices.remove(it) }
        addedSensors.forEach { iotDevices.add(it) }
        sensors = iotDevices.filterIsInstance<Sensor>().toMutableList()
    }
}