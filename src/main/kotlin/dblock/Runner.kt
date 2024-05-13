package dblock

import dblock.Task.Status
import dblock.strategy.TaskDispatcher
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.UUID.randomUUID
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.stream.IntStream
import kotlin.time.measureTime

@Component
class Runner(
    private val taskRepository: TaskRepository,
    private val taskDispatchers: List<TaskDispatcher>,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val executor = Executors.newFixedThreadPool(4)

    @EventListener(ApplicationReadyEvent::class)
    fun run() {
        taskDispatchers.forEach(this::tryStrategy)
        executor.shutdown()
    }

    private fun tryStrategy(taskDispatcher: TaskDispatcher) {
        createPendingTasks()
        measureTime {
            val dispatchedTasks = dispatchTasksWithManyDispatchers(taskDispatcher)
            check(taskDispatcher, dispatchedTasks)
        }.also { log.info("Took $it") }
    }

    private fun createPendingTasks() {
        val newTasks = IntStream.range(0, 1000)
            .mapToObj { Task(randomUUID(), Status.TODO) }
            .toList()
        taskRepository.deleteAll()
        taskRepository.saveAll(newTasks)
    }

    private fun dispatchTasksWithManyDispatchers(taskDispatcher: TaskDispatcher): List<List<Task>> {
        val futures = ArrayList<Future<List<Task>>>()
        for (i in 1..100) {
            val future = executor.submit<List<Task>> {
                dispatchTasks(taskDispatcher)
            }
            futures.add(future)
        }
        return futures.map { it.get() }.toList()
    }

    private fun dispatchTasks(taskDispatcher: TaskDispatcher): List<Task> {
        return try {
            taskDispatcher.dispatchPendingTasks()
        } catch (e: Exception) {
            dispatchTasks(taskDispatcher)
        }
    }

    private fun check(taskDispatcher: TaskDispatcher, dispatchedTasks: List<List<Task>>) {
        log.info("*** ${taskDispatcher.javaClass.simpleName} was checked ***")
        val totalTasks = dispatchedTasks.sumOf { group -> group.size }
        val uniqueTasks = dispatchedTasks.flatten().distinctBy { it.id }.count()
        if (totalTasks == uniqueTasks) {
            log.info("It works!")
        } else {
            log.error("$uniqueTasks tasks taken instead of $totalTasks :(")
        }
        log.info("Pending tasks: {}", taskRepository.countByStatus(Status.TODO))
        log.info("Tasks in progress: {}", taskRepository.countByStatus(Status.IN_PROGRESS))
    }
}
