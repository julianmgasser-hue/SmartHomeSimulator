package commands

import Command
import devices.Sensor
import functionalities.LiquidDispensable

class DispenseLiquidCommand(
    private val device : LiquidDispensable,
    private val influencedSensors : List<Sensor>
): Command {
    override fun execute() {
        device.dispense(influencedSensors)
    }
}