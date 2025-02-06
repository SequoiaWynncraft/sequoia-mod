import java.io.File
import java.net.URL

plugins {
    id("java")
    id("idea")
    id("fabric-loom") version ("1.8.9")
}

repositories {
    maven("https://maven.parchmentmc.org/")
    maven("https://maven.wispforest.io")
}

val MINECRAFT_VERSION: String by rootProject.extra
val PARCHMENT_VERSION: String? by rootProject.extra
val FABRIC_LOADER_VERSION: String by rootProject.extra
val FABRIC_API_VERSION: String by rootProject.extra

val WYNNTILS_VERSION: String by rootProject.extra
val WYNNTILS = {
    val url =
        "https://github.com/Wynntils/Wynntils/releases/download/v$WYNNTILS_VERSION/wynntils-$WYNNTILS_VERSION-fabric+MC-$MINECRAFT_VERSION.jar"
    val file = File(projectDir, "libs/wynntils-$WYNNTILS_VERSION.jar")

    file.parentFile.mkdirs()

    if (!file.exists()) {
        URL(url).openStream().use { downloadStream ->
            file.outputStream().use { fileOut ->
                downloadStream.copyTo(fileOut)
            }
        }

        val modsFile = File(projectDir, "run/mods/wynntils.jar")
        modsFile.parentFile.mkdirs()
        file.inputStream().use { input ->
            modsFile.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }

    files(file.absolutePath)
}
val OWO_LIB_VERSION: String by rootProject.extra
val WEBSOCKET_VERSION: String by rootProject.extra

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = MINECRAFT_VERSION)
    mappings(loom.layered() {
        officialMojangMappings()
        if (PARCHMENT_VERSION != null) {
            parchment("org.parchmentmc.data:parchment-${MINECRAFT_VERSION}:${PARCHMENT_VERSION}@zip")
        }
    })

    modCompileOnly("net.fabricmc:fabric-loader:$FABRIC_LOADER_VERSION")

    fun addDependentFabricModule(name: String) {
        val module = fabricApi.module(name, FABRIC_API_VERSION)
        modCompileOnly(module)
    }

    modImplementation(WYNNTILS())
    modImplementation("io.wispforest:owo-lib:${OWO_LIB_VERSION}")
    annotationProcessor("io.wispforest:owo-lib:${OWO_LIB_VERSION}")
    implementation("org.java-websocket:Java-WebSocket:$WEBSOCKET_VERSION")
}

loom {
    mixin {
        defaultRefmapName = "sequoia.refmap.json"
        useLegacyMixinAp = false
    }

    accessWidenerPath = file("src/main/resources/sequoia.accessWidener")

    mods {
        val main by creating { // To match the default mod generated for Forge
            sourceSet("main")
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