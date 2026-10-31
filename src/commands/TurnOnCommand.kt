package commands

import Command
import functionalities.Switchable

class TurnOnCommand(
    private val device : Switchable
): Command {

    override fun execute() {
        device.turnOn()
    }

}