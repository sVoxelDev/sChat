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
    implementation(libs.gradle.plugin.testlog)
    implementation(libs.gradle.plugin.lombok)
    implementation(libs.gradle.plugin.shadow)

    implementation(files(libs.javaClass.protectionDomain.codeSource.location))
}