class Queue {
    private val actionQueue = mutableListOf<Action>()

    fun getNextAction(): Action?{
        return if (actionQueue.isEmpty()) null else actionQueue.first()
    }

    fun addAction(action: Action){
        actionQueue.add(action)
    }
}