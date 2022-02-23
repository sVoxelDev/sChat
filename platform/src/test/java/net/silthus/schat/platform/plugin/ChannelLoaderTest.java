package net.silthus.schat.platform.plugin;

import org.junit.jupiter.api.BeforeEach;

class ChannelLoaderTest {

    @BeforeEach
    void setUp() {
        // TODO: Bugs
        //  - reload von configs überschreibt bestehende settings -> copyFrom verwenden
        //  - <channel.name> placeholder existiert/funktioniert nicht - default in config fixen
        //  - global private chat: channel uid angezeigt anstatt display name
    }
}