import me.modmuss50.mpp.ReleaseType

plugins {
    id("java")
    id("idea")
    id("fabric-loom") version ("1.9.2")
}

val MINECRAFT_VERSION: String by rootProject.extra
val PARCHMENT_VERSION: String? by rootProject.extra
val FABRIC_LOADER_VERSION: String by rootProject.extra
val FABRIC_API_VERSION: String by rootProject.extra

val MOD_ID: String by rootProject.extra
val MOD_VERSION: String by rootProject.extra

val WEBSOCKET_VERSION: String by rootProject.extra

base {
    archivesName.set("sequoia-fabric")
}

dependencies {
    minecraft("com.mojang:minecraft:${MINECRAFT_VERSION}")
    mappings(loom.layered {
        officialMojangMappings()
        if (PARCHMENT_VERSION != null) {
            parchment("org.parchmentmc.data:parchment-${MINECRAFT_VERSION}:${PARCHMENT_VERSION}@zip")
        }
    })
    modImplementation("net.fabricmc:fabric-loader:$FABRIC_LOADER_VERSION")

    fun addEmbeddedFabricModule(name: String) {
        val module = fabricApi.module(name, FABRIC_API_VERSION)
        modImplementation(module)
        include(module)
    }

    fun addCompileOnlyFabricModule(name: String) {
        val module = fabricApi.module(name, FABRIC_API_VERSION)
        modCompileOnly(module)
    }

    fun addFabricModule(name: String) {
        val module = fabricApi.module(name, FABRIC_API_VERSION)
        modImplementation(module)
    }

    addCompileOnlyFabricModule("fabric-transfer-api-v1")

    implementation("com.google.code.findbugs:jsr305:3.0.1")

    implementation(project.project(":common").sourceSets.getByName("main").output)

    include("org.java-websocket:Java-WebSocket:$WEBSOCKET_VERSION")
}

tasks.named("compileTestJava").configure {
    enabled = false
}

tasks.named("test").configure {
    enabled = false
}

// https://fabricmc.net/wiki/tutorial:mixin_hotswaps
afterEvaluate {
    loom.runs.configureEach {
        vmArg("-javaagent:${ configurations.compileClasspath.get().find { it.name.contains("sponge-mixin") } }")
    }
}

loom {
    if (project(":common").file("src/main/resources/sequoia.accessWidener").exists())
        accessWidenerPath.set(project(":common").file("src/main/resources/sequoia.accessWidener"))

    mixin {
        useLegacyMixinAp = false
    }

    runs {
        create("fabricClient") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("run")
        }
        create("fabricServer") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("run")
        }
    }
}

tasks {
    processResources {
        from(project.project(":common").sourceSets.main.get().resources)
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(mapOf("mod_id" to MOD_ID, "mod_version" to MOD_VERSION))
        }
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(zipTree(project.project(":common").tasks.jar.get().archiveFile))
    }

    remapJar.get().destinationDirectory = rootDir.resolve("build").resolve("libs")
}

publishMods {
    val mcVersionSequoiaVersion = "mc$MINECRAFT_VERSION-$MOD_VERSION"
    version = "$mcVersionSequoiaVersion-fabric"
    file = tasks.remapJar.get().archiveFile
    changelog = rootProject.file("CHANGELOG.md").readText().trim()
    type = getReleaseType()
    modLoaders.add("fabric")
    modLoaders.add("quilt")

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_API_KEY")
        projectId = "fn9R8LGk"
        minecraftVersions.add(MINECRAFT_VERSION)
        displayName = "Sequoia $MOD_VERSION for Fabric"
    }
}

fun getReleaseType(): ReleaseType {
    return when (val releaseType = providers.environmentVariable("RELEASE_TYPE").orNull) {
        "alpha"-> ReleaseType.ALPHA
        "beta" -> ReleaseType.BETA
        "stable" -> ReleaseType.STABLE
        else -> {
            if (releaseType != null)
                throw IllegalArgumentException("Release type must be alpha, beta or stable!")

            ReleaseType.STABLE
        }
    }
}