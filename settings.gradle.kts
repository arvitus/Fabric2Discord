rootProject.name = "Fabric2Discord"
pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        mavenCentral()
        gradlePluginPortal()
    }

    val loom_version = providers.gradleProperty("loom_version").get()
    val fabric_kotlin_version = providers.gradleProperty("fabric_kotlin_version").get()
    plugins {
        id("net.fabricmc.fabric-loom") version loom_version
        id("org.jetbrains.kotlin.jvm") version
                fabric_kotlin_version
                    .split("+kotlin.")[1] // Grabs the sentence after `+kotlin.`
                    .split("+")[0] // Ensures sentences like `+build.1` are ignored
    }
}