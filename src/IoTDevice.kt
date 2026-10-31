import java.util.UUID

open class IoTDevice (
    open val name : String,
    open val id : UUID = UUID.randomUUID()
)