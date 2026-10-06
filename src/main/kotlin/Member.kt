interface Printable {
    fun printInfo()
}

abstract class Member(val name: String) : Printable {
    abstract val maxTasks: Int
}

class Developer(name: String) : Member(name) {
    override val maxTasks = 3
    override fun printInfo() {
        println("Developer: $name (max $maxTasks tasks)")
    }
}

class Intern(name: String) : Member(name) {
    override val maxTasks = 1
    override fun printInfo() {
        println("Intern: $name (max $maxTasks task)")
    }
}
