import me.modmuss50.mpp.ReleaseType
import java.net.URI

val modId: String by rootProject.extra
val modVersion: String by rootProject.extra

val minecraftVersion: String by rootProject.extra
val parchmentVersion: String? by rootProject.extra
val fabricLoaderVersion: String by rootProject.extra
val fabricApiVersion: String by rootProject.extra
val neoForgeEventBusVersion: String by rootProject.extra

val wynntilsVersion: String by rootProject.extra
val wynntils = {
    val url =
        "https://github.com/Wynntils/Wynntils/releases/download/v$wynntilsVersion/wynntils-$wynntilsVersion-fabric+MC-$minecraftVersion.jar"
    val file = File(projectDir, "libs/wynntils-$wynntilsVersion.jar")

    file.parentFile.mkdirs()

    if (!file.exists()) {
        URI.create(url).toURL().openStream().use { downloadStream ->
            file.outputStream().use { fileOut ->
                downloadStream.copyTo(fileOut)
            }
        }
    }

    files(file.absolutePath)
}
val webSocketVersion: String by rootProject.extra
val devAuthVersion: String by rootProject.extra
val owoLibVersion: String by rootProject.extra

plugins {
    id("java")
    id("idea")
    id("fabric-loom") version ("1.9.2")
}

base {
    archivesName.set("sequoia-fabric")
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings(loom.layered {
        officialMojangMappings()
        if (parchmentVersion != null) {
            parchment("org.parchmentmc.data:parchment-${minecraftVersion}:${parchmentVersion}@zip")
        }
    })
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    fun addEmbeddedFabricModule(name: String) {
        val module = fabricApi.module(name, fabricApiVersion)
        modImplementation(module)
        include(module)
    }

    fun addCompileOnlyFabricModule(name: String) {
        val module = fabricApi.module(name, fabricApiVersion)
        modCompileOnly(module)
    }

    fun addFabricModule(name: String) {
        val module = fabricApi.module(name, fabricApiVersion)
        modImplementation(module)
    }

    addCompileOnlyFabricModule("fabric-transfer-api-v1")

    implementation("com.google.code.findbugs:jsr305:3.0.1")

    implementation(project.project(":common").sourceSets.getByName("main").output)

    include("org.java-websocket:Java-WebSocket:$webSocketVersion")
    implementation("org.java-websocket:Java-WebSocket:$webSocketVersion")

    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:${devAuthVersion}")

    implementation("net.neoforged:bus:${neoForgeEventBusVersion}") {
        exclude("org.ow2.asm")
        exclude("org.apache.logging.log4j")
        exclude("cpw.mods", "modlauncher")
    }

    implementation(wynntils())

    modImplementation("io.wispforest:owo-lib:${owoLibVersion}")
    annotationProcessor("io.wispforest:owo-lib:${owoLibVersion}")
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
        vmArg("-javaagent:${configurations.compileClasspath.get().find { it.name.contains("sponge-mixin") }}")
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
            vmArg("-Ddevauth.configDir=${rootProject.file(".devauth").absolutePath}")
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
            expand(mapOf("mod_id" to modId, "mod_version" to modVersion))
        }
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(zipTree(project.project(":common").tasks.jar.get().archiveFile))
    }

    remapJar.get().destinationDirectory = rootDir.resolve("build").resolve("libs")
}

publishMods {
    val mcVersionSequoiaVersion = "mc$minecraftVersion-$modVersion"
    version = "$mcVersionSequoiaVersion-fabric"
    file = tasks.remapJar.get().archiveFile
    changelog = rootProject.file("CHANGELOG.md").readText().trim()
    type = getReleaseType()
    modLoaders.add("fabric")
    modLoaders.add("quilt")

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_API_KEY")
        projectId = "fn9R8LGk"
        minecraftVersions.add(minecraftVersion)
        displayName = "Sequoia $modVersion for Fabric"
    }
}

fun getReleaseType(): ReleaseType {
    return when (val releaseType = providers.environmentVariable("RELEASE_TYPE").orNull) {
        "alpha" -> ReleaseType.ALPHA
        "beta" -> ReleaseType.BETA
        "stable" -> ReleaseType.STABLE
        else -> {
            if (releaseType != null)
                throw IllegalArgumentException("Release type must be alpha, beta or stable!")

            ReleaseType.STABLE
        }
    }
}