import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("java")
    id("fabric-loom") version "1.9.2" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.1" apply false
    id("com.diffplug.spotless") version "6.25.0"
    id("org.sonarqube") version "6.0.1.5171"
}

val MOD_ID by extra { "sequoia" }
// https://semver.org/
val MOD_VERSION by extra { "0.6.4" }

// Fabric: https://fabricmc.net/develop/
// Neoforge: https://neoforged.net/
val MINECRAFT_VERSION by extra { "1.21.4" } // MUST manually update fabric.mod.json and neoforge.mods.toml
val NEOFORGE_VERSION by extra { "21.4.51-beta" }
val NEOFORGE_EVENTBUS_VERSION by extra { "8.0.2" }
val FABRIC_LOADER_VERSION by extra { "0.16.10" }
val FABRIC_API_VERSION by extra { "0.110.5+1.21.4" }
val PARCHMENT_VERSION by extra { null }

val WYNNTILS_VERSION by extra { "3.0.6" }
val OWO_LIB_VERSION by extra { "0.12.20+1.21.4" }
val WEBSOCKET_VERSION by extra { "1.5.7" }
val DEV_AUTH_VERSION by extra { "1.2.1" }

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.diffplug.spotless")

    repositories {
        maven("https://maven.parchmentmc.org/")
        maven("https://jitpack.io")
        maven("https://maven.wispforest.io")
        maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
        gradlePluginPortal()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        withSourcesJar()
    }

    tasks.named("assemble") {
        dependsOn("spotlessApply")
    }

    configure<SpotlessExtension> {
        java {
            importOrder()
            removeUnusedImports()
            palantirJavaFormat("2.47.0")
            trimTrailingWhitespace()
            endWithNewline()
            custom("Refuse wildcard imports") { input ->
                if (Regex("""\nimport .*\*;""").containsMatchIn(input)) {
                    throw AssertionError("Do not use wildcard imports. 'spotlessApply' cannot resolve this issue.")
                }
                input
            }
            custom("Refuse IntelliJ annotations") { input ->
                if (Regex("""\nimport org\.jetbrains\.annotations\.""").containsMatchIn(input)) {
                    throw AssertionError("Do not use IntelliJ annotations. 'spotlessApply' cannot resolve this issue.")
                }
                input
            }
            custom("No empty line after opening curly brace") { input ->
                input.replace(Regex("""\{\n\n"""), "{\n")
            }
            licenseHeader(
                """
                /*
                 * Copyright © sequoia-mod ${'$'}YEAR.
                 * This file is released under LGPLv3. See LICENSE for full license details.
                 */
                """.trimIndent()
            ).updateYearWithLatest(true)
        }
        json {
            target("src/**/*.json")
            gson().indentWithSpaces(2).sortByKeys().version("2.11.0")
            trimTrailingWhitespace()
            endWithNewline()
        }
        format("lang") {
            target("src/main/resources/assets/sequoia/lang/*.json")
            custom("No empty language json files") { input ->
                input.replace(Regex("^\\{\\}\\n\$"), "")
            }
        }
        groovyGradle {
            target("**/*.gradle")
            greclipse("4.27").configFile(rootDir.resolve("greclipse.properties"))
            trimTrailingWhitespace()
            endWithNewline()
        }
        format("misc") {
            target("*.gradle", "*.md", ".gitignore", "*.properties")
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }
        flexmark {
            target("**/*.md")
            flexmark()
        }
        yaml {
            target("**/*.yml", "**/*.yaml")
            jackson()
        }
    }
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "me.modmuss50.mod-publish-plugin")

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

    fun createVersionString(): String {
        val builder = StringBuilder()
        val isReleaseBuild = providers.environmentVariable("RELEASE_WORKFLOW").isPresent
        val buildId = System.getenv("GITHUB_RUN_NUMBER")
        if (isReleaseBuild) {
            builder.append(MOD_VERSION)
        } else {
            builder.append(MOD_VERSION.substringBefore('-'))
            builder.append("-SNAPSHOT")
        }
        builder.append("+mc").append(MINECRAFT_VERSION)
        if (!isReleaseBuild) {
            if (buildId != null) {
                builder.append("-build.$buildId")
            } else {
                builder.append("-local")
            }
        }
        return builder.toString()
    }

    tasks.processResources {
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(mapOf("version" to createVersionString()))
        }
    }

    version = createVersionString()
    group = "dev.lotnest.sequoia"

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    tasks.withType<GenerateModuleMetadata>().configureEach {
        enabled = false
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isReproducibleFileOrder = true
        isPreserveFileTimestamps = false
    }
}

tasks.register("printMinecraftVersion") {
    doLast { println(MINECRAFT_VERSION) }
}

tasks.register("printModVersion") {
    doLast { println(MOD_VERSION) }
}
