plugins {
    id("schat.api")
}

description = "Base implementation of sChat for supporting various platforms"

dependencies {
    api(project(":schat-core"))
    api(project(":schat-ui"))
    api(project(":schat-features"))

    implementation(libs.adventure.platform)
    implementation(libs.adventure.minimessage)
    implementation(libs.bundles.cloud.commands)
    implementation(libs.bundles.configurate)

    testImplementation(testFixtures(project(":schat-core")))
    testImplementation(testFixtures(project(":schat-ui")))

    testFixturesImplementation(testFixtures(project(":schat-core")))
    testFixturesImplementation(testFixtures(project(":schat-ui")))

    testFixturesImplementation(libs.adventure.minimessage)
    testFixturesImplementation(libs.bundles.cloud.commands)
}
