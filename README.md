# Task Manager

A Kotlin console app that simulates a small team task manager: developers and interns get tasks assigned, search the task list and see statistics.

## How to run

Open the project in IntelliJ IDEA, open `Main.kt` and click ▶ next to `main()`.

Or from the terminal:

```bash
./gradlew run
```

The app does not require user input. `main()` runs a fixed scenario: it loads the tasks, assigns them (showing success, already assigned task, unknown id and limit exceeded), searches the tasks and prints statistics.

## Requirements

- **Data class:** `Task`
- **Sealed class:** `TaskResult`
- **Inheritance, interface, polymorphism:** `Member`, `Developer`, `Intern`, `Printable`
- **List, Set, Map:** `TaskManager` (tasks, priorities, assigned tasks)
- **map, filter, reduce:** `getPriorities()`, `findTasks()`, `totalHours()`
- **Higher-order function and lambda:** `findTasks { it.hours > 2 }`
- **Conditions and loops:** `assignTask()`, `for` loop in `main()`
- **Suspend function and coroutine:** `loadTasks()`, `runBlocking` and `launch` in `main()`
