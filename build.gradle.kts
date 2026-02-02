plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.paperweight.userdev)
}

group = "me.zetastormy"
version = property("projectVersion") as String
description = "A modern Minecraft server hub core solution. Based on DeluxeHub by ItsLewizzz."

val libsPackage = property("libsPackage") as String

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    exclusiveContent {
        forRepository {
            mavenCentral()
        }
        filter {
            includeGroup("net.kyori")
            includeGroup("net.megavex")
            includeGroup("com.arcaniax")
            includeGroup("com.github.cryptomorin")
            includeGroup("io.github.miniplaceholders")
        }
    }

    exclusiveContent {
        forRepository {
            maven {
                url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
            }
        }
        filter {
            includeGroup("me.clip")
        }
    }

    exclusiveContent {
        forRepository {
            maven {
                url = uri("https://jitpack.io")
            }
        }
        filter {
            includeGroup("com.github.koca2000")
        }
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")

    implementation(libs.configurate.yaml)
    // Interfaces support
    //implementation("org.spongepowered:configurate-extra-interface:$configurateVersion")
    //implementation("org.spongepowered:configurate-extra-interface-ap:$configurateVersion")

    implementation(libs.scoreboard.library.api)
    runtimeOnly(libs.scoreboard.library.implementation)
    runtimeOnly(variantOf(libs.scoreboard.library.modern) { classifier("mojmap") })

    compileOnly(platform(libs.adventure.bom))
    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)

    compileOnly(libs.placeholderapi)
    compileOnly(libs.head.database.api)

    compileOnly(libs.xseries)

    compileOnly(libs.miniplaceholders.api)
    compileOnly(libs.noteblockapi)
}

configurations.implementation {
    exclude("org.bukkit", "bukkit")
}

configurations.all {
    resolutionStrategy {
        force("org.spongepowered:configurate-yaml:4.2.0-GeyserMC-SNAPSHOT")
    }
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

        relocate("net.megavex.scoreboardlibrary", "${libsPackage}.net.megavex.scoreboardlibrary")

        relocate("org.spongepowered.configurate", "${libsPackage}.org.spongepowered.configurate")
        relocate("io.leangen.geantyref", "${libsPackage}.io.leangen.geantyref")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
