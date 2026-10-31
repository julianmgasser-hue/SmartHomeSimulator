package frames

import functionalities.Measurable

class RefreshFrame(
    private val sensors : List<Measurable>
) : Frame(){
    override fun execute() {
        sensors.forEach { sensor ->
            sensor.refreshData()
        }
    }
}