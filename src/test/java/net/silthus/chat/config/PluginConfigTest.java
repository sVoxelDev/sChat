/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.chat.config;

import net.silthus.chat.TestBase;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;

import static net.silthus.chat.Constants.Formatting.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class PluginConfigTest extends TestBase {

    @Test
    void create() {
        YamlConfiguration file = YamlConfiguration.loadConfiguration(new File("src/main/resources/config.yml"));
        PluginConfig config = PluginConfig.config(file);

        assertThat(config.channels())
                .hasSizeGreaterThanOrEqualTo(1)
                .containsKey("global");

        ConsoleConfig consoleConfig = config.console();
        assertThat(consoleConfig)
                .isNotNull()
                .extracting(ConsoleConfig::defaultChannel)
                .isEqualTo("global");
        assertThat(config.formats())
                .containsKeys("default", "channel", "none", "sender", "sender_hover", "channel_formatted");
    }

    // adjust the 'config.yml' defaults... values or the default properties in the config object if this test fails
    @Test
    void defaultValues() {
        PluginConfig config = plugin.getPluginConfig();
        assertThat(config.defaults().channel())
                .isEqualTo(ChannelConfig.channelConfig(new MemoryConfiguration()));
    }

    @Test
    void withDifferentDefaultValues() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("defaults.channel.protect", true);
        cfg.set("channels.test.name", "Test");
        final PluginConfig pluginConfig = PluginConfig.config(cfg);

        assertThat(pluginConfig.defaults().channel().protect()).isTrue();
        assertThat(pluginConfig.channels())
                .containsOnly(entry("test", ChannelConfig.builder().protect(true).name("Test").build()));
    }

    @Test
    void loads_default_Formats() {
        final PluginConfig pluginConfig = PluginConfig.config(new MemoryConfiguration()).registerFormatTemplates();

        assertThat(pluginConfig.formats()).isNotEmpty()
                .containsKeys(DEFAULT, CHANNEL, NO_FORMAT);
    }

    @Test
    void loadsCustomFormats() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("formats.test.format", "foobar: <message>");
        final PluginConfig config = PluginConfig.config(cfg);
        assertThat(config.formats())
                .extractingByKey("test")
                .isNotNull()
                .extracting(FormatConfig::toFormat)
                .extracting("format")
                .isEqualTo("foobar: <message>");
    }

    @Test
    void allows_overwriting_defaultFormat() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("formats.none.format", "<gray><message>");
        final PluginConfig config = PluginConfig.config(cfg);
        assertThat(config.formats())
                .extractingByKey("none")
                .isNotNull()
                .extracting(FormatConfig::toFormat)
                .extracting("format")
                .isEqualTo("<gray><message>");
    }
}