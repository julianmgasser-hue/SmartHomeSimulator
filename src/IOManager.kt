import devices.Sensor
import frames.Frame
import kotlin.collections.sortedBy

class IOManager {

    fun displayData(sensors: List<Sensor>) {
        sensors.sortedBy { it.sensorType }.forEach {
            println("${it.name}: ${it.readData()}")
        }
    }

    fun userAction(devices : List<IoTDevice>): Frame?{
        println("Want to take an Action? (Y/n)")
        if(readln().lowercase() == "n") return null

        println("What action?")
        val userInputWords = readln().lowercase().trim().split(Regex("\\s+"))
        if(userInputWords.filter{it.isNotBlank()}.isEmpty()) {
            println("seems like you don't want to make a relevant action")
        }
        val action = userInputWords.first()
        if (action != "read" && action != "edit" && action != "work") {
            manual()
            return null
        }
        if (userInputWords.size < 2){
            manual()
            println("your supposed to enter 2 words!")
            return null
        }
        val device = userInputWords[1]
        parseUserAction(action, device, devices)
        return null //temporary, remove!!!
    }

    fun parseUserAction(verb : String, device : String, devices: List<IoTDevice>){

    }

    fun manual(){
        println("""
            STRUCTURE:
            <Verb> <DeviceName/Id>
            
            Verbs:
            help -> this instruction
            read -> read a sensors instruction
            edit -> manipulate data of sensors
            work -> add an Action to all actions 
            
            DeviceName/Id 
            either the exact name of the Device (case sensitive)
            or the uuid of the Device
        """.trimIndent())
    }

}