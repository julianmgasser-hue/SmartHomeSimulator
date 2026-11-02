package commands

import Command
import IoTDevice

class CreateDeviceCommand(
    private val device : IoTDevice,
    private val ioTDevices : MutableList<IoTDevice>
): Command {
    override fun execute() {
        ioTDevices.add(device)
    }

}