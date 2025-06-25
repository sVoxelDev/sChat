plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.plugin.indra)
    implementation(libs.gradle.plugin.indra.publishing)
    implementation(libs.gradle.plugin.indra.crossdoc)
    implementation("org.cadixdev.licenser:org.cadixdev.licenser.gradle.plugin:0.6.1")

    implementation(libs.gradle.plugin.testlog)
    implementation(libs.gradle.plugin.lombok)
    implementation(libs.gradle.plugin.shadow)
    implementation(libs.gradle.plugin.cucumber)

    implementation(files(libs.javaClass.protectionDomain.codeSource.location))
}