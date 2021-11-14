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

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import net.silthus.chat.Constants;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNullElseGet;
import static java.util.stream.Collectors.toMap;

@Data
@Builder
@Accessors(fluent = true)
@Log(topic = Constants.PLUGIN_NAME)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PluginConfig {

    public static PluginConfig fromConfig(ConfigurationSection config) {
        return new PluginConfig(config);
    }

    private final Defaults defaults;
    private final ConsoleConfig console;
    private final PrivateChatConfig privateChat;
    @Singular
    private final Map<String, ChannelConfig> channels;

    private PluginConfig(@NonNull ConfigurationSection config) {
        this.defaults = loadDefaults(config.getConfigurationSection("defaults"));
        this.console = loadConsoleConfig(config.getConfigurationSection("console"));
        this.privateChat = loadPrivateChatConfig(config.getConfigurationSection("private_chats"));
        this.channels = loadChannels(config.getConfigurationSection("channels"));
    }

    private Defaults loadDefaults(ConfigurationSection config) {
        if (config == null) {
            warnSectionNotDefined("defaults");
            return new Defaults(ChannelConfig.defaults());
        }
        return new Defaults(ChannelConfig.of(requireNonNullElseGet(config.getConfigurationSection("channel"), () -> config.createSection("channel"))));
    }

    private ConsoleConfig loadConsoleConfig(ConfigurationSection config) {
        if (config == null) {
            warnSectionNotDefined("console");
            return new ConsoleConfig();
        }
        return new ConsoleConfig(config);
    }

    private PrivateChatConfig loadPrivateChatConfig(ConfigurationSection config) {
        if (config == null) {
            warnSectionNotDefined("private_chats");
            return new PrivateChatConfig();
        }
        return new PrivateChatConfig(config);
    }

    private Map<String, ChannelConfig> loadChannels(ConfigurationSection config) {
        if (config == null) {
            warnSectionNotDefined("channels");
            return new HashMap<>();
        }
        return config.getKeys(false).stream()
                .collect(toMap(
                        key -> key,
                        key -> defaults.channel.withConfig(config.getConfigurationSection(key)).build()
                ));
    }

    private void warnSectionNotDefined(String section) {
        log.warning("No '" + section + "' section found inside your config.yml! Make sure your config is up-to-date with the config.default.yml.");
    }

    public record Defaults(ChannelConfig channel) {

    }
}
