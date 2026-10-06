# Kotlin Task Manager

A small Kotlin console application that manages a to-do list. It creates work, personal and deadline tasks, updates their progress, filters/sorts/groups them, prints a workload report and finally sends "reminders" and a fake "cloud sync" concurrently using coroutines.

## How to run

Requirements: JDK 17+ (no separate Kotlin or Gradle install needed, the Gradle wrapper is included).

```bash
./gradlew run
```

If Java is not on your `PATH`, point `JAVA_HOME` to your JDK first, e.g. on macOS:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./gradlew run
```

You can also open the folder in IntelliJ IDEA and run `main()` in `Main.kt`.

## Project structure

```
src/main/kotlin/taskmanager/
├── Models.kt       – enum, sealed classes, data class, interfaces, task class hierarchy
├── TaskManager.kt  – task storage, collection operations, suspend functions
└── Main.kt         – main() entry point, demo scenario, coroutines
```

## Where the requirements are demonstrated

| Requirement | Where |
|---|---|
| Variables & data types | `Main.kt`: `val appName: String`, `val maxDailyMinutes: Int`, `var day`, `var remaining`; `Task.status` is a `var`; `Boolean`, `Int`, `Long`, `String` used throughout |
| Conditions | `if` in `TaskManager.add/updateProgress`, `DeadlineTask.estimatedMinutes`; `when` in `Task.statusText()`, `printResult()` and the report verdict in `Main.kt` |
| Loops | `for` loops in `printTasks()` and over the grouped map, `while` loop splitting work into days, `forEach` |
| List, Set, Map | `MutableList<Task>` and `MutableMap<Int, Task>` in `TaskManager`; `tags: Set<String>` on each task; `groupByCategory(): Map<String, List<Task>>`, `countByPriority: Map<Priority, Int>` |
| map / filter / reduce | `TaskManager.find` (`filter`), `mapTasks` (`map`), `report()` (`map` + `fold`, `flatMap`, `associateWith`, `count`), `remainingWorkload()` (`filter` + `map` + `reduce`) |
| Functions, higher-order functions, lambdas | `find(predicate: (Task) -> Boolean)`, `mapTasks(transform: (Task) -> R)`, `sortedBy(selector)`, `section(title, block: () -> Unit)`; called with lambdas like `manager.find { "study" in it.tags }` |
| Classes and objects | `TaskManager` class, `object IdGenerator` (singleton), `object TaskStatus.Todo` |
| Inheritance | `abstract class Task` extended by `WorkTask`, `PersonalTask`, `DeadlineTask` |
| Interfaces & polymorphism | `Describable` and `Schedulable` interfaces implemented by `Task`; each subclass overrides `estimatedMinutes()` / `describe()` and they are all used through a single `List<Task>` |
| Data class | `TaskReport` (printed with its auto-generated `toString()`), plus `TaskStatus.InProgress`, `OperationResult.Success`, etc. |
| Sealed class | `TaskStatus` (Todo / InProgress / Done) and `OperationResult` (Success / NotFound / Failure), handled with exhaustive `when` |
| Suspend function & coroutine | `TaskManager.sendReminder()` and `syncWithCloud()` are `suspend` functions using `delay`; `main()` uses `runBlocking`, `launch` for the sync job and `async` + `awaitAll` for parallel reminders |

The Kotlin Coroutines dependency (`kotlinx-coroutines-core`) is added in `build.gradle.kts`.
