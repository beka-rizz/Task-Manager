class TaskManager(val tasks: List<Task>) {
    private val assigned = mutableMapOf<Member, MutableList<Task>>()

    fun assignTask(member: Member, taskId: Int): TaskResult {
        val task = tasks.find { it.id == taskId } ?: return TaskResult.NotFound(taskId)
        if (assigned.values.flatten().any { it.id == taskId }) {
            return TaskResult.AlreadyAssigned(task)
        }
        if ((assigned[member]?.size ?: 0) >= member.maxTasks) {
            return TaskResult.LimitExceeded(member)
        }
        assigned.getOrPut(member) { mutableListOf() }.add(task)
        return TaskResult.Success(task)
    }

    fun findTasks(predicate: (Task) -> Boolean): List<Task> {
        return tasks.filter(predicate)
    }

    fun getPriorities(): Set<String> {
        return tasks.map { it.priority }.toSet()
    }

    fun totalHours(): Int {
        return tasks.map { it.hours }.reduce { acc, hours -> acc + hours }
    }
}
