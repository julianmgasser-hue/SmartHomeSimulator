package functionalities

interface Measurable {
    fun readData() : Double
    fun updateData()
    fun influenceData(newData : Double)
}