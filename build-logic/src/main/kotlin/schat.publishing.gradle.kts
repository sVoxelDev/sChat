plugins {
    id("net.kyori.indra.publishing")
}

if (System.getenv("CI").toBoolean()) {
    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    if (signingKey != null && signingPassword != null) {
        signing.useInMemoryPgpKeys(signingKey, signingPassword)
    }
}

indra {
    javaVersions {
        target(17)
    }

    github("sVoxelDev", "sChat") {
        ci(true)
    }

    mitLicense()

    configurePublications {
        pom {
            developers {
                developer {
                    id.set("Silthus")
                    name.set("Michael Reichenbach")
                    timezone.set("Europe/Berlin")
                }
            }
        }
    }
}