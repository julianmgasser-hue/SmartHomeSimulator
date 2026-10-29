package functionalities

import Command

interface Actuator {
    fun turnOn()
    fun turnOff() : Command
}