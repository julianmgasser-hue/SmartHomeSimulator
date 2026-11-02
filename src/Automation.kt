import frames.Frame

class Automation (
    private val condition : () -> Boolean,
    private val action : Frame
) {
    fun checkAutomation() = if (condition()) action else null
}