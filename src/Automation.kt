import frames.Frame

class Automation (
    val condition : () -> Boolean,
    val action : Frame
) {
    fun checkAutomation() = if (condition()) action else null
}