package dblock.strategy

import dblock.Task
import dblock.Task.Status
import dblock.TaskProcessor
import dblock.TaskRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class LockTaskDispatcher(
    private val taskRepository: TaskRepository,
    private val taskProcessor: TaskProcessor,
) : TaskDispatcher {

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    override fun dispatchPendingTasks(): List<Task> {
        val pendingTasks = taskRepository.findPendingTasksWithLock()
        pendingTasks.forEach { task ->
            task.status = Status.IN_PROGRESS
            taskProcessor.initiateProcessing(task)
        }
        return pendingTasks
    }
}
