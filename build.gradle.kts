plugins {
    java
    id("com.gradleup.shadow") version ("9.2.2")
    id("io.papermc.paperweight.userdev") version ("2.0.0-beta.19")
}

group = "team.devblook"
version = property("projectVersion") as String
description = "A modern Minecraft server hub core solution. Based on DeluxeHub by ItsLewizzz."

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
    paperweight.paperDevBundle("1.21.10-R0.1-SNAPSHOT")

    implementation("javax.inject:javax.inject:1")

    implementation("net.megavex:scoreboard-library-api:2.4.3")
    runtimeOnly("net.megavex:scoreboard-library-implementation:2.4.3")
    runtimeOnly("net.megavex:scoreboard-library-modern:2.4.3:mojmap")

    compileOnly("net.kyori:adventure-text-minimessage:4.25.0")
    compileOnly("net.kyori:adventure-api:4.25.0")

    compileOnly("me.clip:placeholderapi:2.11.7")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
    implementation("com.github.cryptomorin:XSeries:13.5.1")
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:3.1.0")
    compileOnly("com.github.koca2000:NoteBlockAPI:1.6.3")
}

configurations.implementation {
    exclude("org.bukkit", "bukkit")
}

tasks {
    processResources {
        filesMatching("paper-plugin.yml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("Akropolis-${project.version}.jar")

        minimize {
            exclude(dependency("net.megavex:.*:.*"))
        }

        relocate("net.megavex.scoreboardlibrary", "${libsPackage}.scoreboardlibrary")
        relocate("com.cryptomorin.xseries", "${libsPackage}.xseries")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
