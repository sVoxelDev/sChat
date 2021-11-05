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

import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Accessors(fluent = true)
public class PluginConfig {

    public static PluginConfig fromConfig(ConfigurationSection config) {
        return new PluginConfig(config);
    }

    private ConsoleConfig console;
    private Map<String, ChannelConfig> channels = new HashMap<>();

    private PluginConfig(ConfigurationSection config) {
        loadConsoleConfig(config);
        loadChannels(config);
    }

    private void loadConsoleConfig(ConfigurationSection config) {
        this.console = new ConsoleConfig(Objects.requireNonNullElseGet(
                config.getConfigurationSection("console"),
                () -> config.createSection("console")
        ));
    }

    private void loadChannels(ConfigurationSection config) {
        ConfigurationSection channels = config.getConfigurationSection("channels");
        if (channels != null) {
            for (String key : channels.getKeys(false)) {
                this.channels.put(key, ChannelConfig.of(channels.getConfigurationSection(key)));
            }
        }
    }
}
