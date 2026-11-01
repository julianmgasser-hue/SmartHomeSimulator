package frames

import Command
import IoTDevice

data class ActionFrame(
    val device : IoTDevice,
    val command : Command
): Frame()
{
    override fun execute() {
        command.execute()
    }

}
