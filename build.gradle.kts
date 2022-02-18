plugins {
    id("schat.publishing")
    alias(libs.plugins.indra.publishing.sonatype)
}

group = "net.silthus.schat"
description = "A unique chat plugin for Minecraft servers."

indraSonatype {
    useAlternateSonatypeOSSHost("s01")
}
