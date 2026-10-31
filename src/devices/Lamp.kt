package devices

import IoTDevice
import functionalities.Switchable
import java.util.UUID

data class Lamp (override val name : String, override val id : UUID = UUID.randomUUID(), private var isOn: Boolean): Switchable, IoTDevice(name, id){
    override fun turnOn() {
        isOn = true
    }

    override fun turnOff() {
        isOn = false
    }
}