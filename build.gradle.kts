import net.fabricmc.loom.configuration.IncludeConfigurations
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm")
    id("net.fabricmc.fabric-loom")
    `maven-publish`
    java
    id("com.gradleup.shadow") version "9.6.1"
}

group = property("maven_group")!!
version = "${property("mod_version")}+${property("minecraft_version_group")}"

repositories {
    mavenCentral()
    maven("https://maven.shedaniel.me/")
    maven("https://maven.nucleoid.xyz/")
    maven("https://jitpack.io")
    maven("https://repo.spongepowered.org/maven/")
}

val fapiModules: Array<String> = arrayOf(
    "fabric-api-base",
    "fabric-command-api-v2",
)

dependencies {
    fun includeImplement(any: Any) {
        include(implementation(any)!!)
    }

    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    fapiModules.forEach {
        includeImplement(fabricApi.module(it, property("fabric_api_version").toString()))
    }

    // Our classes end up relocated onto the kotlinx copies bundled with kord below, so the newer
    // kotlinx versions from fabric-language-kotlin must not leak onto the compile classpath.
    include(implementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}") {
        isTransitive = false
    })
    includeImplement("eu.pb4:placeholder-api:${property("placeholder_api_version")}")

    // Shadow and include some extra dependencies
    includeImplement("org.spongepowered:configurate-extra-kotlin:4.1.2")
    implementation(shadow("org.spongepowered:configurate-core:4.1.2")!!)
    implementation(shadow("org.spongepowered:configurate-hocon:4.1.2")!!)
    implementation(shadow("dev.kord:kord-core:0.17.0")!!)
    implementation(shadow("com.vdurmont:emoji-java:5.1.1")!!)
}

java {
    withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
        // has to match the kotlin-stdlib that gets shaded in with kord
        apiVersion = KotlinVersion.KOTLIN_2_2
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.release = 25
    }

    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(getProperties())
            expand(mutableMapOf("version" to project.version))
        }
    }

    shadowJar {
        archiveClassifier = ""
        relocationPrefix = "dependencies"
        configurations = listOf(project.configurations.shadow.get())
        enableAutoRelocation = true

        exclude(
            "**/META-INF/**",
            "DebugProbesKt.bin",
            "org/intellij/**",
            "org/jetbrains/annotations/**",
            "*.kotlin_module"
        )
        minimize()
    }

    jar {
        archiveClassifier = "dev"
        from("LICENSE")
    }

    assemble {
        dependsOn(shadowJar)
    }
}

// No remapJar on 26.x anymore, so the include() jars have to be nested into the shadow jar ourselves.
IncludeConfigurations.nestJars(project, tasks.shadowJar, configurations.include)

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.shadowJar)
            artifact(tasks.kotlinSourcesJar)
        }
    }

    repositories {
        // mavenLocal()
    }
}
