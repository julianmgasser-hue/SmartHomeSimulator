import frames.Frame

class Queue<T>{
    val queue = mutableListOf<T>()

    fun add(item : T){
        queue.add(item)
    }

    fun remove(): T? {
        if (queue.isEmpty()) return null
        return queue.removeFirst()
    }

    fun addImportant(item : T){
        queue.addFirst(item)
    }

    fun contains(item : T): Boolean{
        return queue.any{it == item}
    }
}