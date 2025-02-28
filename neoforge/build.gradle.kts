import me.modmuss50.mpp.ReleaseType
import java.net.URI

val modVersion: String by rootProject.extra

val minecraftVersion: String by rootProject.extra
val parchmentVersion: String? by rootProject.extra
val neoForgeVersion: String by rootProject.extra

val wynntilsVersion: String by rootProject.extra
val wynntils = {
    val url =
        "https://github.com/Wynntils/Wynntils/releases/download/v$wynntilsVersion/wynntils-$wynntilsVersion-neoforge+MC-$minecraftVersion.jar"
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
val devAuthVersion: String by rootProject.extra
val webSocketVersion: String by rootProject.extra

plugins {
    id("java")
    id("idea")
    id("net.neoforged.moddev") version "2.0.42-beta"
}

base {
    archivesName = "sequoia-neoforge"
}

repositories {
    maven("https://maven.pkg.github.com/ims212/Forge_Fabric_API") {
        credentials {
            username = "IMS212"
            // Read-only token
            password = "ghp_" + "DEuGv0Z56vnSOYKLCXdsS9svK4nb9K39C1Hn"
        }
    }
    maven("https://maven.su5ed.dev/releases")
    maven("https://maven.neoforged.net/releases/")

    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

tasks.jar {
    val main = project.project(":common").sourceSets.getByName("main")
    from(main.output.classesDirs) {
        exclude("/sequoia.refmap.json")
    }
    from(main.output.resourcesDir)

    from(rootDir.resolve("LICENSE.md"))

    filesMatching("neoforge.mods.toml") {
        expand(mapOf("version" to modVersion))
    }
}

tasks.jar.get().destinationDirectory = rootDir.resolve("build").resolve("libs")

neoForge {
    version = neoForgeVersion

    if (parchmentVersion != null) {
        parchment {
            minecraftVersion = minecraftVersion
            mappingsVersion = parchmentVersion
        }
    }

    runs {
        configureEach {
            dependencies {
                runtimeOnly("org.java-websocket:Java-WebSocket:$webSocketVersion")
            }
        }

        create("client") {
            client()
            jvmArgument("-Ddevauth.configDir=${rootProject.file(".devauth").absolutePath}")
        }
        create("server") {
            server()
        }
    }

    mods {
        create("sequoia") {
            sourceSet(project.sourceSets.main.get())
            sourceSet(project.project(":common").sourceSets.main.get())
        }
    }
}

tasks.named("compileTestJava").configure {
    enabled = false
}

dependencies {
    compileOnly(project.project(":common").sourceSets.getByName("main").output)

    implementation("org.java-websocket:Java-WebSocket:$webSocketVersion")

    implementation(wynntils())

    runtimeOnly("me.djtheredstoner:DevAuth-neoforge:${devAuthVersion}")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

publishMods {
    val mcVersionSequoiaVersion = "mc$minecraftVersion-$modVersion"
    version = "$mcVersionSequoiaVersion-neoforge"
    file = tasks.jar.get().archiveFile
    changelog = rootProject.file("CHANGELOG.md").readText().trim()
    type = getReleaseType()
    modLoaders.add("neoforge")

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_API_KEY")
        projectId = "fn9R8LGk"
        minecraftVersions.add(minecraftVersion)
        displayName = "Sequoia $modVersion for Neoforge"
    }
}

fun getReleaseType(): ReleaseType {
    return when (val releaseType = providers.environmentVariable("RELEASE_TYPE").orNull) {
        "alpha" -> ReleaseType.ALPHA
        "beta" -> ReleaseType.BETA
        "stable" -> ReleaseType.STABLE
        else -> {
            if (releaseType != null) {
                throw IllegalArgumentException("Release type must be alpha, beta or stable!")
            }
            ReleaseType.STABLE
        }
    }
}