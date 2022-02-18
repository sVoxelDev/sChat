enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "sChat"

moonshineProject("bom")
moonshineProject("core")
moonshineProject("features")
moonshineProject("ui")
moonshineProject("platform")
moonshineProject("bukkit")
moonshineProject("velocity")
moonshineProject("bungeecord")

fun moonshineProject(path: String, name: String = "schat-$path"): ProjectDescriptor {
    include(path)
    val project = project(":$path")
    project.name = name
    return project
}