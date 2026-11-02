import commands.CreateDeviceCommand
import commands.DispenseLiquidCommand
import commands.InfluenceDataCommand
import commands.TurnOffCommand
import commands.TurnOnCommand
import devices.Sensor
import devices.SensorType
import frames.ActionFrame
import frames.DisplayFrame
import frames.Frame
import functionalities.Displayable
import functionalities.LiquidDispensable
import functionalities.Measurable
import functionalities.Switchable
import kotlin.collections.sortedBy

class IOManager {
    val deviceFactory = DeviceFactory()

    fun displayData(sensors: List<Sensor>) {
        sensors.sortedBy { it.sensorType }.forEach {
            println("${it.name}: ${it.readData()}")
        }
    }

    fun userAction(devices: MutableList<IoTDevice>): Frame? {
        println("Want to take an Action? (y/N)")
        if (readln().lowercase() == "y") return null

        println("What action?")
        val userInputWords = readln().lowercase().trim().split(Regex("\\s+"))
        if (userInputWords.filter { it.isNotBlank() }.isEmpty()) {
            println("seems like you don't want to make a relevant action")
        }
        val action = userInputWords.first()
        if (action == "make") {
            return createDevice(devices)
        }
        if (action != "read" && action != "edit" && action != "work") {
            manual()
            return null
        }
        if (userInputWords.size < 2) {
            manual()
            println("your supposed to enter 2 words!")
            return null
        }
        val device = userInputWords.drop(1).joinToString(" ")
        return parseUserAction(action, device, devices)
    }

    fun parseUserAction(verb: String, device: String, devices: List<IoTDevice>): Frame? {
        when (verb) {
            "read" -> {
                if (device == "all") devices.forEach { it.displayBasicInfos() }
                val desiredDevice = findDevice(device, devices)
                if (desiredDevice != null) {
                    val displayable = desiredDevice as? Displayable ?: return null
                    return DisplayFrame(listOf(displayable))
                }
            }

            "edit" -> {
                val desiredDevice = findDevice(device, devices)
                if (desiredDevice != null) {
                    val sensor = desiredDevice as? Sensor ?: return null
                    println("Enter a new Value for ${sensor.name}:")
                    val newData = readln().toDoubleOrNull() ?: return null
                    return ActionFrame(sensor, InfluenceDataCommand(sensor, newData))
                }
            }

            "work" -> {
                val desiredDevice = findDevice(device, devices)
                if (desiredDevice != null) {
                    val availableActions = getAvailableActions(desiredDevice)
                    availableActions.forEachIndexed { index, action ->
                        println("$index $action")
                    }
                    println("Enter Number of Action:")
                    val desiredAction = readln().toIntOrNull() ?: return null
                    if (desiredAction !in availableActions.indices) {
                        println("Invalid Action Number")
                        return null
                    }
                    val actionAsString = availableActions[desiredAction]
                    when (actionAsString) {
                        "Einschalten" -> return ActionFrame(
                            desiredDevice,
                            TurnOnCommand(desiredDevice as Switchable)
                        )

                        "Ausschalten" -> return ActionFrame(
                            desiredDevice,
                            TurnOffCommand(desiredDevice as Switchable)
                        )

                        "Sensordaten Beeinflussen" -> {
                            println("Was sollte der neue Wert sein?")
                            val newData = readln().toDoubleOrNull() ?: return null
                            return ActionFrame(
                                desiredDevice,
                                InfluenceDataCommand(desiredDevice as Measurable, newData)
                            )
                        }

                        "Flüssigkeit spendieren" -> {
                            println("Für welchen Sensor sollte die Flüssigkeit spendiert werden?")
                            val influencedDevice = findDevice(readln(), devices)
                            if (influencedDevice != null) {
                                val sensor = desiredDevice as? Sensor ?: return null
                                return ActionFrame(
                                    desiredDevice,
                                    DispenseLiquidCommand(desiredDevice as LiquidDispensable, listOf(sensor))
                                )
                            }
                        }
                    }
                }
            }

            else -> return null
        }
        return null
    }

    fun findDevice(deviceDescription: String, devices: List<IoTDevice>): IoTDevice? {
        devices.forEach {
            if (it.id.toString().lowercase() == deviceDescription) return it
            if (it.name.lowercase() == deviceDescription) return it
        }
        return null
    }

    fun getAvailableActions(device: IoTDevice): List<String> { //Only Actions that are available in Commands
        val actions = mutableListOf<String>()
        if (device is Switchable) {
            actions.add("Einschalten")
            actions.add("Ausschalten")
        }

        if (device is Measurable) {
            actions.add("Sensordaten Beeinflussen")
        }

        if (device is LiquidDispensable) {
            actions.add("Flüssigkeit spendieren")
        }

        return actions
    }

    fun createDevice(devices: MutableList<IoTDevice>): Frame? {
        println("Enter the Type of device, you want to create")
        val options = DeviceType.entries
        options.forEachIndexed { index, type ->
            println("$index: $type")
        }
        println("Enter Number of the Type:")
        val desiredAction = readln().toIntOrNull() ?: return null
        if (desiredAction !in options.indices) {
            println("Invalid Number")
            return null
        }
        val type = options[desiredAction]
        println("How do you want to name your Device?")
        val name = readln()
        val newDevice = if (type == DeviceType.SENSOR){
            val typeOptions = SensorType.entries
            println("Enter the Type of Sensor, you want to create")
            typeOptions.forEachIndexed { index, sensorType ->
                println("$index: $sensorType")
            }
            println("Enter Number of Sensor Type:")
            val desiredAction = readln().toIntOrNull() ?: return null
            if (desiredAction !in typeOptions.indices) {
                println("Invalid Number")
                return null
            }
            deviceFactory.createDevice(type, name,  typeOptions[desiredAction])
        } else deviceFactory.createDevice(type, name)

        return ActionFrame(newDevice, CreateDeviceCommand(newDevice, devices))
    }

    fun manual() {
        println(
            """
            STRUCTURE:
            <Verb> <DeviceName/Id>
            
            Verbs:
            help -> this instruction
            read -> read a sensors instruction 
            edit -> manipulate data of sensors
            work -> add an Action to all actions 
            make -> creates a new Device (doesn't require a second word)
            
            DeviceName/Id 
            either the exact name of the Device (case insensitive)
            or the uuid of the Device
            
            you can show every device, when you write "read all"
        """.trimIndent()
        )
    }

}