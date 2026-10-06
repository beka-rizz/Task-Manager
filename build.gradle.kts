plugins {
    kotlin("jvm") version "2.2.20"
    application
}

group = "kz.kbtu"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("taskmanager.MainKt")
}
