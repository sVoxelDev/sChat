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

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static net.silthus.chat.config.ChannelConfig.channelDefaults;
import static net.silthus.chat.config.ConfigUtils.getSection;

@Value
@With
@Builder(toBuilder = true)
@Accessors(fluent = true)
public class WorldGuardConfig {

    public static WorldGuardConfig worldGuardConfig(PluginConfig.Defaults defaults, ConfigurationSection config) {
        return worldGuardDefaults().withConfig(defaults, config).build();
    }

    public static WorldGuardConfig worldGuardDefaults() {
        return builder().build();
    }

    @Builder.Default
    boolean enabled = true;
    @Builder.Default
    ChannelConfig defaultChannelConfig = channelDefaults();
    Map<String, ChannelConfig> regionConfigs;

    public WorldGuardConfig.WorldGuardConfigBuilder withConfig(PluginConfig.Defaults defaults, ConfigurationSection config) {
        final ChannelConfig defaultChannelConfig = defaults.channel().withConfig(getSection(config, "channel_configs.default")).build();
        return toBuilder()
                .enabled(config.getBoolean("enabled", enabled))
                .defaultChannelConfig(defaultChannelConfig)
                .regionConfigs(loadRegionConfigs(getSection(config, "channel_configs.regions", true), defaultChannelConfig));
    }

    private Map<String, ChannelConfig> loadRegionConfigs(@NonNull ConfigurationSection section, ChannelConfig defaultChannelConfig) {
        return section.getKeys(false).stream()
                .collect(toMap(
                        key -> key,
                        key -> defaultChannelConfig.withConfig(getSection(section, key, true)).build()
                ));
    }

    public ChannelConfig regionConfig(String region) {
        return regionConfigs.getOrDefault(region, defaultChannelConfig);
    }
}
