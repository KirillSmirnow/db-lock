package dblock.strategy

import dblock.Task

interface TaskDispatcher {

    fun dispatchPendingTasks(): List<Task>
}
