import java.io.ByteArrayOutputStream

plugins {
    java
    id("com.gradleup.shadow") version ("9.3.0")
    id("io.papermc.paperweight.userdev") version ("2.0.0-beta.19")
}

group = "me.zetastormy"
description = "A modern Minecraft server hub core solution. Based on DeluxeHub by ItsLewizzz."

version = buildString {
    fun git(vararg args: String): String {
        return providers.exec {
            commandLine("git", *args)
        }.standardOutput.asText.get().trim()
    }

    val latestTag = git("describe", "--tags", "--abbrev=0").replace("v", "")

    append(latestTag)

    val branchName = git("rev-parse", "--abbrev-ref", "HEAD")

    if (branchName != "stable" && !branchName.startsWith("release")) {
        val commitHash = git("rev-parse", "--short", "HEAD")

        append("+").append(commitHash)

        val gitStatus = git("status", "--porcelain")

        if (!gitStatus.isEmpty()) {
            append(".dirty")
        }
    }
}

val scoreboardLibraryVersion = "2.4.4"

val libsPackage = property("libsPackage") as String

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
}

dependencies {
    paperweight.paperDevBundle("1.21.6-R0.1-SNAPSHOT")

    implementation("javax.inject:javax.inject:1")

    implementation("net.megavex:scoreboard-library-api:$scoreboardLibraryVersion")
    runtimeOnly("net.megavex:scoreboard-library-implementation:$scoreboardLibraryVersion")
    runtimeOnly("net.megavex:scoreboard-library-modern:$scoreboardLibraryVersion:mojmap")

    compileOnly(platform("net.kyori:adventure-bom:4.23.0"))
    compileOnly("net.kyori:adventure-text-minimessage")
    compileOnly("net.kyori:adventure-api")

    compileOnly("me.clip:placeholderapi:2.11.7")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")

    // Dependency downloaded at runtime, also change
    // the version in AkropolisPluginLoader.java
    // when upgrading
    compileOnly("com.github.cryptomorin:XSeries:13.6.0")

    compileOnly("io.github.miniplaceholders:miniplaceholders-api:3.1.0")
    compileOnly("com.github.koca2000:NoteBlockAPI:1.6.3")
}

configurations.implementation {
    exclude("org.bukkit", "bukkit")
}

tasks {
    val projectVersion = project.version.toString()

    processResources {
        filesMatching("paper-plugin.yml") {
            expand("version" to projectVersion)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("Akropolis-${projectVersion}.jar")

        minimize {
            exclude(dependency("net.megavex:.*:.*"))
        }

        relocate("net.megavex.scoreboardlibrary", "${libsPackage}.scoreboardlibrary")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
