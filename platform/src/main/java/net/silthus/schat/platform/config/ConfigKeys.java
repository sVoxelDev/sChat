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
import java.util.List;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.platform.config.adapter.ConfigurationSection;
import net.silthus.schat.platform.config.key.ConfigKey;
import net.silthus.schat.platform.config.key.KeyedConfiguration;
import net.silthus.schat.pointer.Settings;

import static net.silthus.schat.platform.config.key.ConfigKeyFactory.key;
import static net.silthus.schat.platform.config.key.ConfigKeyFactory.modifiable;

public final class ConfigKeys {

    private ConfigKeys() {
    }

    public static final ConfigKey<List<Channel>> CHANNELS = modifiable(key(config -> {
        final ArrayList<Channel> channels = new ArrayList<>();
        for (final String key : config.getKeys("channels", new ArrayList<>())) {
            channels.add(createFromConfig(config.scoped("channels." + key), key));
        }
        return channels;
    }), (c, value) -> {
        for (final Channel channel : value) {
            c.set("channels." + channel.getKey() + ".name", channel.getDisplayName());
            c.set("channels." + channel.getKey() + ".settings", channel.getSettings());
        }
    });

    /**
     * A list of the keys defined in this class.
     */
    private static final List<? extends ConfigKey<?>> KEYS = KeyedConfiguration.initialise(ConfigKeys.class);

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }

    private static Channel createFromConfig(ConfigurationSection config, String key) {
        final Component name = config.get("name", Component.class);
        Settings settings = config.get("settings", Settings.class);
        if (settings == null)
            settings = Settings.createSettings();
        return Channel.channel(key).name(name).settings(settings).create();
    }

    public static final class InvalidConfig extends RuntimeException {
    }
}
