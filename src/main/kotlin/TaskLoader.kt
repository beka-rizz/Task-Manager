import kotlinx.coroutines.delay

suspend fun loadTasks(): List<Task> {
    println("Loading tasks...")
    delay(1000)
    return listOf(
        Task(1, "Fix login bug", "High", 3),
        Task(2, "Write unit tests", "Medium", 2),
        Task(3, "Update README", "Low", 1),
        Task(4, "Design main screen", "High", 5),
        Task(5, "Code review", "Medium", 1),
    )
}
