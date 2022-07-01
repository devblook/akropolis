plugins {
    java
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

group = "fun.lewisdev"
version = property("projectVersion") as String
description = "An all-in-one hub management system."

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
    mavenCentral()
}

dependencies {
    implementation("javax.inject:javax.inject:1")

    implementation("de.tr7zw:item-nbt-api:2.11.0-SNAPSHOT")
    implementation("fr.mrmicky:fastboard:1.2.1")
    implementation("org.bstats:bstats-bukkit-lite:1.8")
    implementation("com.github.cryptomorin:XSeries:8.8.0")

    compileOnly("net.kyori:adventure-text-minimessage:4.11.0")
    compileOnly("net.kyori:adventure-api:4.11.0")

    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")

    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.1")
}

configurations.implementation {
    exclude("org.bukkit", "bukkit")
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("DeluxeHub-${project.version}.jar")

        minimize()

        relocate("org.bstats", "${libsPackage}.metrics")
        relocate("cl.bgmp", "${libsPackage}.commandframework")
        relocate("de.tr7zw.changeme.nbtapi", "${libsPackage}.nbtapi")
        relocate("fr.mrmicky.fastboard", "${libsPackage}.fastboard")
        relocate("com.cryptomorin.xseries", "${libsPackage}.xseries")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
