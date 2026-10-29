package functionalities
import Command

interface Sensor {
    fun readData() : Command
}