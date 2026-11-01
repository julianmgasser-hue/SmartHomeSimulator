package frames

import functionalities.Displayable

class DisplayFrame(val devices : List<Displayable>): Frame() {
    override fun execute() {
        devices.forEach {
            it.displayData()
        }
    }

}