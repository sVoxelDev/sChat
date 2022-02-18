plugins {
    id("schat.api")
    id("schat.shadow")
}

description = "Bukkit implementation of the sChat platform."

dependencies {
    api(project(":schat-platform"))

    implementation(libs.velocity)
    annotationProcessor(libs.velocity)
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    dependencies {
        include(dependency("net.kyori:event-api:"))
        include(dependency("net.kyori:adventure-text-minimessage:"))
    }

    val lib = "net.silthus.schat.lib"
    relocate("net.kyori.event", "$lib.kyori.event")
    relocate("net.kyori.adventure", "$lib.kyori.adventure")
}
