import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import gradle.kotlin.dsl.accessors._9bc3b6a3f384e4e728217dbc108ce9f4.build
import gradle.kotlin.dsl.accessors._9bc3b6a3f384e4e728217dbc108ce9f4.publish

plugins {
    id("com.github.johnrengelman.shadow")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    dependencies {
        include(project(":schat-core"))
        include(project(":schat-platform"))
        include(project(":schat-ui"))
        include(project(":schat-features"))
        include(dependency("cloud.commandframework::"))
        include(dependency("io.leangen.geantyref::"))
        include(dependency("org.spongepowered::"))
        include(dependency("org.bstats::"))
    }

    val lib = "net.silthus.schat.lib"

    relocate("cloud.commandframework", "$lib.commands")
    relocate("org.spongepowered.configurate", "$lib.configurate")
    relocate("io.leangen.geantyref", "$lib.typetoken")
    relocate("org.bstats", "$lib.bstats")
}

tasks.build {
    dependsOn(tasks.withType<ShadowJar>())
}

tasks.publish {
    dependsOn(tasks.withType<ShadowJar>())
}