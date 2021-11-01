package net.silthus.chat.config;

import net.md_5.bungee.api.ChatColor;
import net.silthus.chat.formats.SimpleFormat;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelConfigTest {

    @Test
    void load_fromConfig() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("name", "Test");
        cfg.set("format.prefix", "foo-");
        cfg.set("format.suffix", "-bar: ");
        cfg.set("format.chat_color", "GREEN");

        ChannelConfig config = ChannelConfig.of(cfg);
        assertThat(config.getName())
                .isEqualTo("Test");
        assertThat(config.getFormat())
                .extracting(
                        SimpleFormat::getPrefix,
                        SimpleFormat::getSuffix,
                        SimpleFormat::getChatColor
                ).contains(
                        "foo-",
                        "-bar: ",
                        ChatColor.GREEN
                );
    }
}