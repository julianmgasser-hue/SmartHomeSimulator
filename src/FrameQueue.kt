import frames.Frame

class FrameQueue(){
    val queue = mutableListOf<Frame>()

    fun add(frame : Frame){
        queue.add(frame)
    }

    fun remove(): Frame? {
        if (queue.isEmpty()) return null
        return queue.removeFirst()
    }

    fun addImportant(frame : Frame){
        queue.addFirst(frame)
    }

    fun contains(frame : Frame): Boolean{
        return queue.any{it == frame}
    }
}