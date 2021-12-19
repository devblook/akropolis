import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version ("7.1.1")
}

group = "fun.lewisdev"
version = property("projectVersion") as String
description = "An all-in-one hub management system."

val libsPackage = property("libsPackage") as String

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.bgmp.cl/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://libraries.minecraft.net/")
    maven("https://papermc.io/repo/repository/maven-public/")
    mavenCentral()
}

dependencies {
    implementation("javax.inject:javax.inject:1")

    implementation("cl.bgmp:command-framework-bukkit:1.0.3-SNAPSHOT") {
        exclude(group = "org.bukkit", module = "bukkit")
    }

    implementation("de.tr7zw:item-nbt-api:2.8.0")
    implementation("fr.mrmicky:fastboard:1.2.0")
    implementation("org.bstats:bstats-bukkit-lite:1.7")
    implementation("com.github.cryptomorin:XSeries:8.5.0.1")

    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-chat:1.17-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("me.clip:placeholderapi:2.10.10")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.1")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<ProcessResources> {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    archiveFileName.set("DeluxeHub-${project.version}.jar")

    minimize()

    relocate("org.bstats", "${libsPackage}.metrics")
    relocate("cl.bgmp", "${libsPackage}.commandframework")
    relocate("de.tr7zw.changeme.nbtapi", "${libsPackage}.nbtapi")
    relocate("fr.mrmicky.fastboard", "${libsPackage}.fastboard")
    relocate("com.cryptomorin.xseries", "${libsPackage}.xseries")
    relocate("com.tchristofferson.configupdater", "${libsPackage}.configupdater")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
