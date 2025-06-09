plugins {
    `java-library`
    `maven-publish`
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.gradleup.shadow") version "8.3.6"
    id("org.beryx.jlink") version "3.1.1"
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

// Configuração do sistema operacional
val isWindows = org.gradle.internal.os.OperatingSystem.current().isWindows
val isMacOs = org.gradle.internal.os.OperatingSystem.current().isMacOsX
val isLinux = org.gradle.internal.os.OperatingSystem.current().isLinux

jlink {
    options.set(listOf("--strip-debug", "--compress", "zip-6", "--no-header-files", "--no-man-pages"))

    launcher {
        name = "Tetris"
        moduleName = "com.uneb.tetris"
        mainClass = "com.uneb.tetris.Main"
    }

    jpackage {
        imageName = "Tetris"
        appVersion = "1.0.0"

        // Configuração específica por plataforma
        when {
            isWindows -> {
                // Windows - configurações específicas
                icon = "src/main/resources/assets/textures/ui/icons/ic_game.ico"
                installerType = "msi"

                // Opções específicas do Windows
                installerOptions.addAll(listOf(
                    "--win-dir-chooser",
                    "--win-menu",
                    "--win-shortcut",
                    "--win-per-user-install",
                    "--win-upgrade-uuid", "7c1c43dd-9ee6-4541-a003-b4459880373a"
                ))

                // Configurações adicionais do Windows
                jvmArgs.addAll(listOf(
                    "-Dfile.encoding=UTF-8",
                    "-Djava.awt.headless=false"
                ))
            }

            isMacOs -> {
                // macOS - configurações específicas
                icon = "src/main/resources/assets/textures/ui/icons/ic_game.icns"
                installerType = "dmg"

                installerOptions.addAll(listOf(
                    "--mac-package-name", "Tetris",
                    "--mac-package-identifier", "com.uneb.tetris"
                ))
            }

            isLinux -> {
                // Linux - configurações específicas
                icon = "src/main/resources/assets/textures/ui/icons/ic_game.png"
                installerType = "deb"

                installerOptions.addAll(listOf(
                    "--linux-package-name", "tetris",
                    "--linux-app-category", "Game",
                    "--linux-shortcut"
                ))
            }
        }

        // Configurações gerais
        vendor = "UNEB"
        description = "Um jogo de Tetris desenvolvido em JavaFX"

        // Argumentos da JVM para todas as plataformas
        jvmArgs.addAll(listOf(
            "--add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
            "--add-opens=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED",
            "--add-opens=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED",
            "--add-opens=javafx.base/com.sun.javafx.runtime=ALL-UNNAMED",
            "-Xmx512m",
            "-Xms256m"
        ))
    }
}

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
                "Implementation-Version" to project.version,
                "Main-Class" to "com.uneb.tetris.Main"
            )
        }
    }

    // Task personalizada para validar ícones
    register("validateIcons") {
        doLast {
            val iconPaths = when {
                isWindows -> listOf("src/main/resources/assets/textures/ui/icons/ic_game.ico")
                isMacOs -> listOf("src/main/resources/assets/textures/ui/icons/ic_game.icns")
                isLinux -> listOf("src/main/resources/assets/textures/ui/icons/ic_game.png")
                else -> emptyList()
            }

            iconPaths.forEach { path ->
                val iconFile = file(path)
                if (!iconFile.exists()) {
                    throw GradleException("Ícone não encontrado: $path")
                } else {
                    println("✓ Ícone encontrado: $path")
                }
            }
        }
    }

    // Task para criar todos os tipos de instalador
    register("buildAllInstallers") {
        dependsOn("validateIcons")
        group = "distribution"
        description = "Constrói instaladores para a plataforma atual"

        doLast {
            println("Construindo instalador para: ${System.getProperty("os.name")}")
        }

        finalizedBy("jpackage")
    }

    build {
        dependsOn("jar")
    }

    // Validar ícones antes de fazer jpackage
    named("jpackage") {
        dependsOn("validateIcons")
    }
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(4, TimeUnit.HOURS)
        cacheDynamicVersionsFor(4, TimeUnit.HOURS)
    }
}