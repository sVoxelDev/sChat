enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "sChat"

sChatProject("bom")
sChatProject("core")
sChatProject("features")
sChatProject("ui")
sChatProject("platform")
sChatProject("bukkit")
sChatProject("velocity")
sChatProject("bungeecord")
sChatProject("acceptance")

fun sChatProject(path: String, name: String = "schat-$path"): ProjectDescriptor {
    include(path)
    val project = project(":$path")
    project.name = name
    return project
}