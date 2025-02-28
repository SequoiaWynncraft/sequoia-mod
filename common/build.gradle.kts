import net.fabricmc.loom.api.LoomGradleExtensionAPI
import java.io.File
import java.net.URI

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
val owoLibVersion: String by rootProject.extra
val webSocketVersion: String by rootProject.extra

plugins {
    id("java")
    id("idea")
    id("fabric-loom") version "1.9.2"
}

repositories {
    maven("https://maven.parchmentmc.org/")
    maven("https://maven.wispforest.io")
}

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = minecraftVersion)
    mappings(loom.layered() {
        officialMojangMappings()
        if (parchmentVersion != null) {
            parchment("org.parchmentmc.data:parchment-${minecraftVersion}:${parchmentVersion}@zip")
        }
    })

    modCompileOnly("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    fun addDependentFabricModule(name: String) {
        val module = fabricApi.module(name, fabricApiVersion)
        modCompileOnly(module)
    }

    implementation("org.java-websocket:Java-WebSocket:$webSocketVersion")

    modImplementation(wynntils())
    modImplementation("io.wispforest:owo-lib:${owoLibVersion}")
    annotationProcessor("io.wispforest:owo-lib:${owoLibVersion}")

    compileOnly("net.neoforged:bus:${neoForgeEventBusVersion}")
}

val usingHotswapAgent = project.hasProperty("sequoia.hotswap") &&
        project.property("sequoia.hotswap") == "true"

extensions.configure<LoomGradleExtensionAPI> {
    mixin {
        defaultRefmapName = "sequoia.refmap.json"
        useLegacyMixinAp = false
    }

    accessWidenerPath.set(file("src/main/resources/sequoia.accessWidener"))

    mods {
        val main by creating {
            sourceSet("main")
        }
    }

    runs {
        named("client") {
            property("devauth.configDir", rootProject.file(".devauth").absolutePath)
            if (project.hasProperty("sequoia.hotswap") &&
                project.property("sequoia.hotswap") == "true"
            ) {
                vmArgs("-XX:+AllowEnhancedClassRedefinition")
                vmArgs("-XX:+ClassUnloading")
                vmArgs("-XX:HotswapAgent=fatjar")
            }
            vmArgs("-ea")
            client()
        }
    }
}

tasks {
    jar {
        from(rootDir.resolve("LICENSE.md"))
    }
}

// Hides common tasks in the IDEA list
tasks.configureEach {
    group = null
}
