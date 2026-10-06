package taskmanager

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun printResult(result: OperationResult) {
    val text = when (result) {
        is OperationResult.Success -> "OK: ${result.message}"
        is OperationResult.NotFound -> "ERROR: task #${result.id} not found"
        is OperationResult.Failure -> "ERROR: ${result.reason}"
    }
    println("  $text")
}

fun section(title: String, block: () -> Unit) {
    println("\n=== $title ===")
    block()
}

fun printTasks(tasks: List<Task>) {
    if (tasks.isEmpty()) println("  (no tasks)")
    for (task in tasks) println("  ${task.describe()}")
}

fun main() = runBlocking {
    val appName: String = "Task Manager"
    val maxDailyMinutes: Int = 480
    val manager = TaskManager()

    println(appName)

    section("Adding tasks") {
        val newTasks: List<Task> = listOf(
            WorkTask("Fix login bug", Priority.HIGH, "Mobile App", storyPoints = 3, tags = setOf("bug", "android")),
            WorkTask("Write unit tests", Priority.MEDIUM, "Mobile App", storyPoints = 2, tags = setOf("testing")),
            PersonalTask("Go to the gym", Priority.LOW, minutes = 90, tags = setOf("health")),
            PersonalTask("Buy groceries", Priority.MEDIUM, minutes = 40, tags = setOf("home")),
            DeadlineTask("Submit Kotlin lab", Priority.HIGH, daysLeft = 1, minutes = 120, tags = setOf("study", "kotlin")),
            DeadlineTask("Prepare presentation", Priority.MEDIUM, daysLeft = 5, minutes = 60, tags = setOf("study")),
            PersonalTask("   ", Priority.LOW, minutes = 10),
        )
        newTasks.forEach { printResult(manager.add(it)) }
    }

    section("Updating tasks") {
        printResult(manager.updateProgress(1, 50))
        printResult(manager.complete(3, note = "1.5h workout"))
        printResult(manager.updateProgress(4, 100))
        printResult(manager.complete(3))
        printResult(manager.updateProgress(2, 150))
        printResult(manager.complete(42))
    }

    section("All tasks (polymorphic describe())") {
        printTasks(manager.all())
    }

    section("High priority, not done (filter with lambda)") {
        printTasks(manager.find { it.priority == Priority.HIGH && !it.isDone })
    }

    section("Tasks tagged 'study'") {
        printTasks(manager.find { "study" in it.tags })
    }

    section("Titles in upper case (map)") {
        println("  " + manager.mapTasks { it.title.uppercase() }.joinToString(", "))
    }

    section("Sorted by priority weight") {
        manager.sortedBy { it.priority.weight }.forEach { println("  ${it.priority} -> ${it.title}") }
    }

    section("Grouped by category (Map)") {
        for ((category, tasks) in manager.groupByCategory()) {
            println("  $category: ${tasks.map { it.title }}")
        }
    }

    section("Report") {
        val report = manager.report()
        println("  $report")
        println("  Completed ${report.completed} of ${report.total}")
        report.countByPriority.forEach { (priority, count) -> println("  $priority: $count") }
        println("  Unique tags (Set): ${report.allTags}")

        val workload = manager.remainingWorkload()
        println("  Weighted remaining workload (reduce): $workload")

        val plannedMinutes = manager.find { !it.isDone }.sumOf { it.estimatedMinutes() }
        val verdict = when {
            plannedMinutes == 0 -> "Nothing left to do!"
            plannedMinutes <= maxDailyMinutes -> "Fits in one day ($plannedMinutes / $maxDailyMinutes min)"
            else -> "Too much for one day: ${plannedMinutes - maxDailyMinutes} min over the limit"
        }
        println("  $verdict")

        var day = 1
        var remaining = plannedMinutes
        while (remaining > 0) {
            val today = minOf(remaining, maxDailyMinutes)
            println("  Day $day: $today min")
            remaining -= today
            day++
        }
    }

    println("\n=== Coroutines: reminders and sync ===")
    val syncJob = launch {
        val synced = manager.syncWithCloud()
        println("  Cloud sync finished: $synced tasks uploaded")
    }

    val reminders = manager.find { !it.isDone }
        .map { task -> async { manager.sendReminder(task) } }
        .awaitAll()
    reminders.forEach { println("  $it") }

    syncJob.join()
    println("\nDone. Goodbye!")
}
