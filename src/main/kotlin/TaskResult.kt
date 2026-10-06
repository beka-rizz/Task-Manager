sealed class TaskResult {
    data class Success(val task: Task) : TaskResult()
    data class AlreadyAssigned(val task: Task) : TaskResult()
    data class LimitExceeded(val member: Member) : TaskResult()
    data class NotFound(val id: Int) : TaskResult()
}
