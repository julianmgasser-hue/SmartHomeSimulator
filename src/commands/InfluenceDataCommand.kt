package commands

import Command
import functionalities.Measurable

class InfluenceDataCommand(
    private val device : Measurable,
    private val newData : Double
): Command {
    override fun execute() {
        device.influenceData(newData)
    }

}