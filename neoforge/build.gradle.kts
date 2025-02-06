import me.modmuss50.mpp.ReleaseType
import java.net.URI

plugins {
    id("idea")
    id("net.neoforged.moddev") version "2.0.42-beta"
    id("java-library")
}

val MINECRAFT_VERSION: String by rootProject.extra
val PARCHMENT_VERSION: String? by rootProject.extra
val NEOFORGE_VERSION: String by rootProject.extra

val MOD_VERSION: String by rootProject.extra

val WYNNTILS_VERSION: String by rootProject.extra
val WYNNTILS = {
    val url =
        "https://github.com/Wynntils/Wynntils/releases/download/v$WYNNTILS_VERSION/wynntils-$WYNNTILS_VERSION-neoforge+MC-$MINECRAFT_VERSION.jar"
    val file = File(projectDir, "libs/wynntils-$WYNNTILS_VERSION.jar")

    file.parentFile.mkdirs()

    if (!file.exists()) {
        URI.create(url).toURL().openStream().use { downloadStream ->
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

base {
    archivesName = "sequoia-neoforge"
}

repositories {
    maven("https://maven.pkg.github.com/ims212/Forge_Fabric_API") {
        credentials {
            username = "IMS212"
            // Read only token
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
        expand(mapOf("version" to MOD_VERSION))
    }
}

tasks.jar.get().destinationDirectory = rootDir.resolve("build").resolve("libs")

neoForge {
    version = NEOFORGE_VERSION

    if (PARCHMENT_VERSION != null) {
        parchment {
            minecraftVersion = MINECRAFT_VERSION
            mappingsVersion = PARCHMENT_VERSION
        }
    }

    runs {
        create("client") {
            client()
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

    implementation(WYNNTILS())
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

publishMods {
    val mcVersionSequoiaVersion = "mc$MINECRAFT_VERSION-$MOD_VERSION"
    version = "$mcVersionSequoiaVersion-neoforge"
    file = tasks.jar.get().archiveFile
    changelog = rootProject.file("CHANGELOG.md").readText().trim()
    type = getReleaseType()
    modLoaders.add("neoforge")

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_API_KEY")
        projectId = "fn9R8LGk"
        minecraftVersions.add(MINECRAFT_VERSION)
        displayName = "Sequoia $MOD_VERSION for Neoforge"
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