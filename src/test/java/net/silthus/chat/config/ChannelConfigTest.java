package net.silthus.chat.config;

import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelConfigTest {

    @Test
    void load_fromConfig() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("name", "Test");
        cfg.set("format", "<message>");

        ChannelConfig config = ChannelConfig.of(cfg);
        assertThat(config.getName())
                .isEqualTo("Test");
        assertThat(config.getFormat())
                .isNotNull();
    }
}