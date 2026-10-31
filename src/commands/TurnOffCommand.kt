package commands

import Command
import functionalities.Switchable

class TurnOffCommand(
    private val device : Switchable
): Command {

    override fun execute() {
        device.turnOff()
    }

}