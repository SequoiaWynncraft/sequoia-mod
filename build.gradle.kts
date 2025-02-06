plugins {
    id("java")
    id("fabric-loom") version ("1.8.9") apply (false)
    id("me.modmuss50.mod-publish-plugin") version ("0.8.1") apply (false)
}

// Fabric: https://fabricmc.net/develop/
// Neoforge: https://neoforged.net/
val MINECRAFT_VERSION by extra { "1.21.4" } //MUST manually update fabric.mod.json and neoforge.mods.toml
val NEOFORGE_VERSION by extra { "21.4.50-beta" }
val FABRIC_LOADER_VERSION by extra { "0.16.9" }
val FABRIC_API_VERSION by extra { "0.110.5+1.21.4" }
val PARCHMENT_VERSION by extra { null }

val MOD_ID by extra { "sequoia" }
val MOD_VERSION by extra { "0.6.3" }

val WYNNTILS_VERSION by extra { "3.0.3" }
val OWO_LIB_VERSION by extra { "0.12.20+1.21.4" }
val WEBSOCKET_VERSION by extra { "1.5.7" }

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.jar {
    enabled = false
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "me.modmuss50.mod-publish-plugin")

    java.toolchain.languageVersion = JavaLanguageVersion.of(21)

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
                builder.append("-build.${buildId}")
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

    // Make builds more reproducible
    tasks.withType<AbstractArchiveTask>().configureEach {
        isReproducibleFileOrder = true
        isPreserveFileTimestamps = false
    }
}

tasks.create("sequoiaPublish") {
    when (val platform = providers.environmentVariable("PLATFORM").orNull) {
        "both" -> {
            dependsOn(tasks.build, ":fabric:publishMods", ":neoforge:publishMods")
        }
        "fabric", "forge" -> {
            dependsOn("${platform}:build", "${platform}:publish", "${platform}:publishMods")
        }
        else -> {
            val isRelease = providers.environmentVariable("RELEASE_WORKFLOW").orNull;
            if (isRelease != null && isRelease == "true")
                throw IllegalStateException("Environment variable PLATFORM cannot be null when running on CI!")
        }
    }
}

tasks.register("printMinecraftVersion") {
    doLast {
        println(MINECRAFT_VERSION)
    }
}

tasks.register("printModVersion") {
    doLast {
        println(MOD_VERSION)
    }
}
