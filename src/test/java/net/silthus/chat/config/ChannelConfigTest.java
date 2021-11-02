package net.silthus.chat.config;

import net.silthus.chat.Channel;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelConfigTest {

    @Test
    void load_fromConfig() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("name", "Test");
        cfg.set("format", "<message>");
        cfg.set("protect", true);
        cfg.set("console", false);
        cfg.set("auto_join", false);

        ChannelConfig config = ChannelConfig.of(cfg);

        assertThat(config.name()).isEqualTo("Test");
        assertThat(config.format()).isNotNull();
        assertThat(config.protect()).isTrue();
        assertThat(config.sendToConsole()).isFalse();
        assertThat(config.autoJoin()).isFalse();
    }

    @Test
    void toChannel_createsChannelWithConfig() {

        Channel channel = ChannelConfig.defaults()
                .name("Test 1")
                .toChannel("test");
        assertThat(channel)
                .extracting(
                        Channel::getIdentifier,
                        Channel::getName
                ).contains(
                        "test",
                        "Test 1"
                );
    }
}