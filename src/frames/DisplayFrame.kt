package frames

import functionalities.Displayable

class DisplayFrame(private val devices : List<Displayable>): Frame() {
    override fun execute() {
        devices.forEach {
            it.displayData()
        }
    }

}