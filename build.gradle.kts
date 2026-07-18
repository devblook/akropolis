import java.io.ByteArrayOutputStream

plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.paperweight.userdev)
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

    exclusiveContent {
        forRepository {
            maven {
                url = uri("https://maven.pkg.github.com/DevBlook/Configurate")
                credentials {
                    username = providers.gradleProperty("gpr.user").get()
                    password = providers.gradleProperty("gpr.token").get()
                }
            }
        }
        filter {
            includeVersionByRegex("org\\.spongepowered", "configurate.*", libs.versions.configurate.get())
        }
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.6-R0.1-SNAPSHOT")

    implementation(libs.configurate.yaml)
    // Interfaces support
    //implementation("org.spongepowered:configurate-extra-interface:$configurateVersion")
    //implementation("org.spongepowered:configurate-extra-interface-ap:$configurateVersion")

    implementation(libs.scoreboard.library.api)
    runtimeOnly(libs.scoreboard.library.implementation)

    compileOnly(platform(libs.adventure.bom))
    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)

    compileOnly(libs.placeholderapi)
    compileOnly(libs.head.database.api)

    implementation(libs.xseries)

    compileOnly(libs.miniplaceholders.api)
    compileOnly(libs.noteblockapi)
}

configurations.implementation {
    exclude("org.bukkit", "bukkit")
}

configurations.all {
    resolutionStrategy {
        force("org.spongepowered:configurate-yaml:${libs.versions.configurate.get()}")
    }
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

        relocate("net.megavex.scoreboardlibrary", "${libsPackage}.net.megavex.scoreboardlibrary")

        relocate("org.spongepowered.configurate", "${libsPackage}.org.spongepowered.configurate")
        relocate("io.leangen.geantyref", "${libsPackage}.io.leangen.geantyref")
        relocate("com.cryptomorin.xseries", "${libsPackage}.xseries")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
