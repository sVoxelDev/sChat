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

package net.silthus.schat.platform.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.silthus.schat.channel.usecases.ChannelConfig;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.key.ConfigKey;
import net.silthus.schat.platform.config.key.KeyedConfiguration;

import static net.silthus.schat.platform.config.key.ConfigKeyFactory.key;
import static net.silthus.schat.platform.config.key.ConfigKeyFactory.modifiable;

public final class ConfigKeys {

    private ConfigKeys() {
    }

    public static final ConfigKey<Map<String, ChannelConfig>> CHANNELS = modifiable(key(config -> {
        final Map<String, ChannelConfig> configs = new HashMap<>();
        for (final String key : config.getKeys("channels", new ArrayList<>())) {
            configs.putIfAbsent(key, createChannelConfig(config, "channels." + key, key));
        }
        return configs;
    }), (c, value) -> {
        for (final Map.Entry<String, ChannelConfig> entry : value.entrySet()) {
            c.set("channels." + entry.getKey(), entry.getValue());
        }
    });

    /**
     * A list of the keys defined in this class.
     */
    private static final List<? extends ConfigKey<?>> KEYS = KeyedConfiguration.initialise(ConfigKeys.class);

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }

    private static ChannelConfig createChannelConfig(ConfigurationAdapter config, String path, String key) {
        final ChannelConfig channelConfig = config.get(path, ChannelConfig.class);
        if (channelConfig == null)
            throw new InvalidConfig();

        channelConfig.setKey(key);
        return channelConfig;
    }

    public static final class InvalidConfig extends RuntimeException {
    }
}
