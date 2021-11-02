package net.silthus.chat.config;

import net.silthus.chat.TestBase;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class PluginConfigTest extends TestBase {

    @Test
    void create() {
        YamlConfiguration file = YamlConfiguration.loadConfiguration(new File("src/main/resources/config.yml"));
        PluginConfig config = PluginConfig.fromConfig(file);

        assertThat(config.channels())
                .hasSizeGreaterThanOrEqualTo(1)
                .containsKey("global");

        ConsoleConfig consoleConfig = config.console();
        assertThat(consoleConfig)
                .isNotNull()
                .extracting(ConsoleConfig::defaultChannel)
                .isEqualTo("global");
    }
}