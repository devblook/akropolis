rootProject.name = "akropolis"
includeBuild("configurate") {
    dependencySubstitution {
        substitute(module("org.spongepowered:configurate-yaml")).using(project(":format:yaml"))
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }
}
