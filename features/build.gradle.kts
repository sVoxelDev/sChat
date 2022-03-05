plugins {
    id("schat.api")
}

description = "Implementation of standalone features for sChat"

dependencies {
    implementation(project(":schat-core"))

    testImplementation(testFixtures(project(":schat-core")))

    testFixturesImplementation(testFixtures(project(":schat-core")))
}