plugins {
    id("schat.publishing")
    `java-platform`
}

description = "Bill of materials for sChat"

indra {
    configurePublications {
        from(components["javaPlatform"])
    }
}

dependencies {
    constraints {
        sequenceOf(
            "core",
            "features",
            "ui",
            "platform",
            "bukkit",
            "velocity",
            "bungeecord"
        ).forEach {
            api(project(":schat-$it"))
        }
    }
}
