import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("schat.api")
    id("schat.shadow")
}

description = "Bukkit implementation of the sChat platform."

dependencies {
    api(project(":schat-platform"))

    implementation(libs.spigot)

    implementation(libs.protocollib)
    implementation(libs.placeholderapi)
    implementation(libs.cloud.commands.paper)
    implementation(libs.adventure.platform.bukkit)
    implementation(libs.bstats.bukkit)

    testImplementation(libs.mockbukkit)
    testImplementation(testFixtures(project(":schat-core")))
    testImplementation(testFixtures(project(":schat-platform")))
    testImplementation(testFixtures(project(":schat-ui")))
}

tasks.withType<ShadowJar> {
    dependencies {
        include(dependency("net.kyori::"))
        include(dependency("org.bstats:bstats-bukkit:"))
    }

    val lib = "net.silthus.schat.lib"
    relocate("net.kyori", "$lib.kyori")
    relocate("org.bstats", "$lib.bstats")
}