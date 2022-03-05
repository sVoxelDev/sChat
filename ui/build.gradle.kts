plugins {
    id("schat.api")
}

description = "UI API and implementation for sChat."

dependencies {
    api(project(":schat-core"))

    implementation(libs.adventure.minimessage)
    implementation(libs.configurate.core)

    testImplementation(libs.adventure.plain)
    testImplementation(libs.adventure.minimessage)
    testImplementation(testFixtures(project(":schat-core")))

    testFixturesImplementation(libs.adventure.plain)
    testFixturesImplementation(libs.adventure.minimessage)
    testFixturesImplementation(testFixtures(project(":schat-core")))
}
