package taskmanager

import kotlinx.coroutines.delay

class TaskManager {
    private val tasks = mutableListOf<Task>()
    private val tasksById = mutableMapOf<Int, Task>()

    fun add(task: Task): OperationResult {
        if (task.title.isBlank()) return OperationResult.Failure("Title must not be empty")
        tasks.add(task)
        tasksById[task.id] = task
        return OperationResult.Success("Added task #${task.id} \"${task.title}\"")
    }

    fun updateProgress(id: Int, percent: Int): OperationResult {
        val task = tasksById[id] ?: return OperationResult.NotFound(id)
        if (percent !in 0..100) return OperationResult.Failure("Progress must be between 0 and 100")
        task.status = if (percent == 100) TaskStatus.Done("finished") else TaskStatus.InProgress(percent)
        return OperationResult.Success("Task #$id progress set to $percent%")
    }

    fun complete(id: Int, note: String = "completed"): OperationResult {
        val task = tasksById[id] ?: return OperationResult.NotFound(id)
        if (task.isDone) return OperationResult.Failure("Task #$id is already done")
        task.status = TaskStatus.Done(note)
        return OperationResult.Success("Task #$id marked as done")
    }

    fun all(): List<Task> = tasks.toList()

    fun find(predicate: (Task) -> Boolean): List<Task> = tasks.filter(predicate)

    fun <R> mapTasks(transform: (Task) -> R): List<R> = tasks.map(transform)

    fun sortedBy(selector: (Task) -> Int): List<Task> = tasks.sortedByDescending(selector)

    fun groupByCategory(): Map<String, List<Task>> = tasks.groupBy { it.category }

    fun report(): TaskReport {
        val totalMinutes = tasks
            .map { it.estimatedMinutes() }
            .fold(0) { acc, minutes -> acc + minutes }

        val countByPriority = Priority.entries.associateWith { p -> tasks.count { it.priority == p } }

        val allTags: Set<String> = tasks.flatMap { it.tags }.toSortedSet()

        return TaskReport(
            total = tasks.size,
            completed = tasks.count { it.isDone },
            totalMinutes = totalMinutes,
            countByPriority = countByPriority,
            allTags = allTags,
        )
    }

    // Weighted score of remaining work: priority weight * minutes, combined with reduce.
    fun remainingWorkload(): Int {
        val pending = tasks.filter { !it.isDone }
        if (pending.isEmpty()) return 0
        return pending
            .map { it.priority.weight * it.estimatedMinutes() }
            .reduce { acc, score -> acc + score }
    }

    suspend fun sendReminder(task: Task): String {
        val waitMs = 100L * (4 - task.priority.weight)
        delay(waitMs)
        return "Reminder: \"${task.title}\" (${task.priority}) - sent after ${waitMs}ms"
    }

    suspend fun syncWithCloud(): Int {
        delay(300)
        return tasks.size
    }
}
