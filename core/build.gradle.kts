plugins {
    id("schat.api")
}

description = "Core API and business logic of sChat"

dependencies {
    api(libs.adventure.api)
    api(libs.event)
    api(libs.gson)

    implementation(libs.adventure.gson)

    testFixturesImplementation(libs.adventure.api)
}