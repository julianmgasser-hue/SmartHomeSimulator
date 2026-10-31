package functionalities

import devices.Sensor

interface LiquidDispensable {
    fun dispense(influencedDevices: List<Sensor>)
}