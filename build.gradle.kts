plugins {
    java
    id("com.github.johnrengelman.shadow") version ("8.1.1")
    id("io.papermc.paperweight.userdev") version ("1.5.10")
}

group = "team.devblook"
version = property("projectVersion") as String
description = "A modern Minecraft server hub core solution. Based on DeluxeHub by ItsLewizz."

val libsPackage = property("libsPackage") as String

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT")

    implementation("javax.inject:javax.inject:1")

    implementation("com.github.MegavexNetwork.scoreboard-library:scoreboard-library-api:2.0.0")
    runtimeOnly("com.github.MegavexNetwork.scoreboard-library:scoreboard-library-implementation:2.0.0")
    runtimeOnly("com.github.MegavexNetwork.scoreboard-library:scoreboard-library-modern:2.0.0")

    compileOnly("org.spongepowered:configurate-hocon:4.1.2")

    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
    compileOnly("net.kyori:adventure-api:4.14.0")

    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")

    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.1")
    compileOnly("com.github.cryptomorin:XSeries:9.7.0")
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
            exclude(dependency("com.github.MegavexNetwork.scoreboard-library:.*:.*"))
        }

        relocate("net.megavex.scoreboardlibrary", "${libsPackage}.scoreboardlibrary")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
