import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version ("7.1.0")
}

group = "fun.lewisdev"
version = property("projectVersion") as String
description = "An all-in-one hub management system."

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
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("cl.bgmp:command-framework-bukkit:1.0.3-SNAPSHOT")
    implementation("de.tr7zw:item-nbt-api:2.8.0")
    implementation("fr.mrmicky:fastboard:1.2.0")
    implementation("org.bstats:bstats-bukkit-lite:1.7")
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-chat:1.17-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("me.clip:placeholderapi:2.10.10")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.0")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<ProcessResources> {
    filter<ReplaceTokens>()
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    minimize()

    exclude(
        "org.bukkit:bukkit:*",
        "org.yaml:snakeyaml:*",
        "com.google.code.gson:gson:*",
        "org.apache.commons:commons-lang:*",
        "de.tr7zw:functional-annotations:*"
    )

    relocate("org.bstats", "fun.lewisdev.deluxehub.libs.metrics")
    relocate("cl.bgmp", "fun.lewisdev.deluxehub.libs.commandframework")
    relocate("de.tr7zw.changeme.nbtapi", "fun.lewisdev.deluxehub.libs.nbtapi")
    relocate("fr.mrmicky.fastboard", "fun.lewisdev.deluxehub.libs.fastboard")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
