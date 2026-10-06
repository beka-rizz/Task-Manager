import kotlinx.coroutines.*

fun printResult(result: TaskResult) {
    when (result) {
        is TaskResult.Success -> println("Success: ${result.task.title}")
        is TaskResult.AlreadyAssigned -> println("Already assigned: ${result.task.title}")
        is TaskResult.LimitExceeded -> println("Limit exceeded: ${result.member.name} (max ${result.member.maxTasks})")
        is TaskResult.NotFound -> println("Not found: id ${result.id}")
    }
}

fun main() = runBlocking {
    val tasks = loadTasks()
    println("Tasks loaded: ${tasks.size}")

    val job = launch {
        delay(500)
        println("\nTasks synced with server")
    }

    val manager = TaskManager(tasks)
    val developer = Developer("Bekarys")
    val intern = Intern("Aruzhan")

    println("\n--- Team ---")
    val members: List<Member> = listOf(developer, intern)
    for (member in members) {
        member.printInfo()
    }

    println("\n--- 1. Success ---")
    printResult(manager.assignTask(developer, 1))

    println("\n--- 2. Already assigned ---")
    printResult(manager.assignTask(intern, 1))

    println("\n--- 3. Not found ---")
    printResult(manager.assignTask(intern, 999))

    println("\n--- 4. Limit exceeded ---")
    printResult(manager.assignTask(intern, 3))
    printResult(manager.assignTask(intern, 5))  // 2nd task, intern limit is 1

    println("\n--- Search ---")
    val highTasks = manager.findTasks { it.priority == "High" }
    println("High priority: ${highTasks.map { it.title }}")

    val longTasks = manager.findTasks { it.hours > 2 }
    println("Longer than 2 hours: ${longTasks.map { it.title }}")

    println("\n--- Statistics ---")
    println("Priorities: ${manager.getPriorities()}")
    println("Total hours: ${manager.totalHours()}")

    job.join()
}
