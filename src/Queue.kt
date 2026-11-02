/**
 * A Queue based on the FIFO principle
 * You can add an Item, with [add]
 * remove it with [remove]
 * and you can even cheat a little, and add an item at the First Spot
 * with [addImportant]. You can check, if an item is in the Queue with [contains]
 * every Class can be used.
 */
class Queue<T>{
    private val queue = mutableListOf<T>()

    /**
     * @param item that will be added to the last entry of the queue.
     */
    fun add(item : T){
        queue.add(item)
    }

    /**
     * @return The First item of the Queue / null if queue is empty
     */
    fun remove(): T? {
        if (queue.isEmpty()) return null
        return queue.removeFirst()
    }

    /**
     * Adds an item in the front of the queue for special use cases.
     * @param item that will be added to the first entry of the queue.
     */
    fun addImportant(item : T){
        queue.addFirst(item)
    }

    /**
     * Checks, if an item is in the queue
     * @return true if item is in queue, false if not
     * @param item that's going to get looked up
     */
    fun contains(item : T): Boolean{
        return queue.any{it == item}
    }
}