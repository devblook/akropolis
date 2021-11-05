plugins {
    java
    `maven-publish`
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

group = "fun.lewisdev"
version = "3.5.3"
description = "DeluxeHub"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
