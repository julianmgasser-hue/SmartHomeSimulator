package frames

import Command
import IoTDevice

data class ActionFrame(
    private val device : IoTDevice,
    val command : Command
): Frame()
{
    override fun execute() {
        command.execute()
    }

}
