plugins {
    `java-library`
    `maven-publish`
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.gradleup.shadow") version "8.3.6"
}

group = "com.uneb"
version = "1.0-SNAPSHOT"
description = "Tetris"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
}

// Versões centralizadas
val versions = mapOf(
    "jackson" to "2.19.0",
    "junit" to "5.12.2",
    "javafx" to "23.0.2"
)

dependencies {
    // Jackson
    listOf("core", "databind", "annotations").forEach {
        api("com.fasterxml.jackson.core:jackson-$it:${versions["jackson"]}")
    }

    // JavaFX
    listOf("controls", "fxml", "media").forEach {
        api("org.openjfx:javafx-$it:${versions["javafx"]}")
    }

    implementation("com.github.almasb:fxgl:21.1") {
        exclude(group = "org.openjfx")
    }

    // Testes
    testImplementation(platform("org.junit:junit-bom:${versions["junit"]}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass = "com.uneb.tetris.Main"
    applicationDefaultJvmArgs = listOf(
        "--add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
        "--add-opens=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED",
        "--add-opens=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED",
        "--add-opens=javafx.base/com.sun.javafx.runtime=ALL-UNNAMED"
    )
}

javafx {
    version = versions["javafx"]
    modules = listOf("controls", "fxml", "media", "graphics", "base").map { "javafx.$it" }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    withJavadocJar()
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.isFork = true
        options.isIncremental = true
        options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
    }

    withType<Test>().configureEach {
        useJUnitPlatform()
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
        reports.html.required = true
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    withType<Javadoc>().configureEach {
        options.encoding = "UTF-8"
    }

    named<Jar>("jar") {
        manifest {
            attributes(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        }
    }

    build {
        dependsOn("jar")
    }
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(4, TimeUnit.HOURS)
        cacheDynamicVersionsFor(4, TimeUnit.HOURS)
    }
}

