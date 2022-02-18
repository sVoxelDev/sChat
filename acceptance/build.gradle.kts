plugins {
    id("schat.api")
    id("se.thinkcode.cucumber-runner")
}

dependencies {
    testImplementation(project(":schat-core"))
    testImplementation(project(":schat-platform"))
    testImplementation(project(":schat-ui"))
    testImplementation(testFixtures(project(":schat-core")))
    testImplementation(testFixtures(project(":schat-platform")))
    testImplementation(testFixtures(project(":schat-ui")))

    testImplementation(libs.junit.platform.suite)

    testImplementation(libs.cucumber)
    testImplementation(libs.cucumber.junit)
    testImplementation(libs.cucumber.guice)

    testImplementation(libs.javax.inject)
    testImplementation(libs.guice)
}

cucumber {
    main = "io.cucumber.core.cli.Main"
    featurePath = "$rootDir/acceptance/src/test/resources/features"
    glue = "classpath:net.silthus.schat.cucumber"
    plugin = arrayOf("pretty", "json:$rootDir/acceptance/build/reports/cucumber-report.json")
}