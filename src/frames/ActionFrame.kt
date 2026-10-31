package frames

import Command
import IoTDevice

data class ActionFrame(
    val device : IoTDevice,
    val parameter: Any?,
    val command : Command
): Frame()
{
    override fun execute() {
        command.execute()
    }

}
