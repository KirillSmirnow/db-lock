package dblock

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface TaskRepository : JpaRepository<Task, UUID> {

    fun countByStatus(status: Task.Status): Long

    @Query("from Task where status = Status.TODO order by id limit 10")
    fun findPendingTasks(): List<Task>
}
