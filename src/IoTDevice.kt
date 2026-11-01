import java.util.UUID

open class IoTDevice (
    open val name : String,
    open val id : UUID = UUID.randomUUID()
){
    fun displayBasicInfos(){
        println("Name: $name, id: $id\n#################")
    }
}