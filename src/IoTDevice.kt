import java.util.UUID

data class IoTDevice (
    val name : String,
    val id : UUID = UUID.randomUUID()
)