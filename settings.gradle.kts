rootProject.name = "akropolis"
includeBuild("configurate") {
    dependencySubstitution {
        substitute(module("org.spongepowered:configurate-yaml")).using(project(":format:yaml"))
    }
}
