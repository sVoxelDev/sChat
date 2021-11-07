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

    @Test
        // adjust the 'config.yml' defaults... values or the default properties in the config object if this test fails
    void defaultValues() {
        PluginConfig config = plugin.getPluginConfig();
        assertThat(config.defaults().channel())
                .isEqualTo(ChannelConfig.of(new MemoryConfiguration()));
    }
}