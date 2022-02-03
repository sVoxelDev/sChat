/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
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
import static net.silthus.schat.platform.config.key.ConfigKeyFactory.lowercaseStringKey;
import static net.silthus.schat.platform.config.key.ConfigKeyFactory.modifiable;
import static net.silthus.schat.platform.config.key.ConfigKeyFactory.notReloadable;

public final class ConfigKeys {

    private ConfigKeys() {
    }

    public static final ConfigKey<String> MESSENGER = notReloadable(lowercaseStringKey("messenger", "pluginmessage"));

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
}
