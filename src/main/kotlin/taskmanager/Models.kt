package taskmanager

enum class Priority(val weight: Int) {
    LOW(1), MEDIUM(2), HIGH(3)
}

sealed class TaskStatus {
    object Todo : TaskStatus()
    data class InProgress(val percent: Int) : TaskStatus()
    data class Done(val note: String) : TaskStatus()
}

sealed class OperationResult {
    data class Success(val message: String) : OperationResult()
    data class NotFound(val id: Int) : OperationResult()
    data class Failure(val reason: String) : OperationResult()
}

data class TaskReport(
    val total: Int,
    val completed: Int,
    val totalMinutes: Int,
    val countByPriority: Map<Priority, Int>,
    val allTags: Set<String>,
)

interface Describable {
    fun describe(): String
}

interface Schedulable {
    fun estimatedMinutes(): Int
}

object IdGenerator {
    private var nextId = 1
    fun next(): Int = nextId++
}

abstract class Task(
    val title: String,
    val priority: Priority,
    val tags: Set<String> = emptySet(),
) : Describable, Schedulable {
    val id: Int = IdGenerator.next()
    var status: TaskStatus = TaskStatus.Todo

    val isDone: Boolean
        get() = status is TaskStatus.Done

    abstract val category: String

    fun statusText(): String = when (val s = status) {
        is TaskStatus.Todo -> "TODO"
        is TaskStatus.InProgress -> "IN PROGRESS (${s.percent}%)"
        is TaskStatus.Done -> "DONE - ${s.note}"
    }

    override fun describe(): String =
        "#$id [$category] $title | ${priority.name} | ${statusText()} | ~${estimatedMinutes()} min"
}

class WorkTask(
    title: String,
    priority: Priority,
    val project: String,
    private val storyPoints: Int,
    tags: Set<String> = emptySet(),
) : Task(title, priority, tags) {
    override val category = "Work"

    override fun estimatedMinutes(): Int = storyPoints * 60

    override fun describe(): String = super.describe() + " | project: $project"
}

class PersonalTask(
    title: String,
    priority: Priority,
    private val minutes: Int,
    tags: Set<String> = emptySet(),
) : Task(title, priority, tags) {
    override val category = "Personal"

    override fun estimatedMinutes(): Int = minutes
}

class DeadlineTask(
    title: String,
    priority: Priority,
    val daysLeft: Int,
    private val minutes: Int,
    tags: Set<String> = emptySet(),
) : Task(title, priority, tags) {
    override val category = "Deadline"

    // Urgent deadlines get extra buffer time for review.
    override fun estimatedMinutes(): Int = if (daysLeft <= 1) minutes + 30 else minutes

    override fun describe(): String {
        val urgency = if (daysLeft <= 1) "URGENT" else "$daysLeft days left"
        return super.describe() + " | $urgency"
    }
}
