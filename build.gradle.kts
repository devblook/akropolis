plugins {
    java
    id("io.github.goooler.shadow") version ("8.1.8")
    id("io.papermc.paperweight.userdev") version ("1.7.2")
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
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")

    implementation("javax.inject:javax.inject:1")

    implementation("net.megavex:scoreboard-library-api:2.1.12")
    runtimeOnly("net.megavex:scoreboard-library-implementation:2.1.12")
    runtimeOnly("net.megavex:scoreboard-library-modern:2.1.12:mojmap")

    //compileOnly("org.spongepowered:configurate-yaml:4.1.2")

    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
    compileOnly("net.kyori:adventure-api:4.17.0")

    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
    compileOnly("com.github.cryptomorin:XSeries:11.2.1")
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:2.2.3")
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
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
