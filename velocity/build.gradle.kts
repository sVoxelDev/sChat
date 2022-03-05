plugins {
    id("schat.api")
    id("schat.shadow")
}

description = "Bukkit implementation of the sChat platform."

dependencies {
    api(project(":schat-platform"))

    implementation(libs.velocity)
    annotationProcessor(libs.velocity)

    implementation(libs.bstats.velocity)
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    dependencies {
        include(dependency("net.kyori:event-api:"))
        include(dependency("net.kyori:adventure-serializer-configurate4:"))
        include(dependency("org.bstats:bstats-velocity:"))
    }

    val lib = "net.silthus.schat.lib"
    relocate("net.kyori.event", "$lib.kyori.event")
    relocate("net.kyori.adventure.serializer.configurate4", "$lib.kyori.adventure.serializer.configurate4")
    relocate("org.bstats", "$lib.bstats")
}
