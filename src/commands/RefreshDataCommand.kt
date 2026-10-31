package commands

import Command
import functionalities.Measurable

class RefreshDataCommand(
    private val device : Measurable
): Command {

    override fun execute() {
        device.refreshData()
    }

}