class Queue {
    private val actionQueue = mutableListOf<Frame>()

    fun getNextAction(): Frame?{
        return if (actionQueue.isEmpty()) null else actionQueue.first()
    }

    fun addAction(action: Frame){
        actionQueue.add(action)
    }
}