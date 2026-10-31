import frames.Frame

class FrameQueue(){
    val queue = mutableListOf<Frame>()

    fun add(frame : Frame){
        queue.add(frame)
    }

    fun remove(): Frame {
        return queue.removeFirst()
    }

    fun addImportant(frame : Frame){
        queue.addFirst(frame)
    }
}