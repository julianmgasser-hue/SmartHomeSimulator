package functionalities

interface Measurable {
    fun readData() : Double
    fun refreshData()
    fun influenceData(newData : Double)
}