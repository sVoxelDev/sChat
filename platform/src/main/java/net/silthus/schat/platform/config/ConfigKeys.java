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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.java.Log;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.key.ConfigKey;
import net.silthus.schat.platform.config.key.KeyedConfiguration;
import net.silthus.schat.platform.locale.Messages;
import net.silthus.schat.ui.view.ViewConfig;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNullElse;
import static net.silthus.schat.platform.config.key.ConfigKeyFactory.booleanKey;
import static net.silthus.schat.platform.config.key.ConfigKeyFactory.key;
import static net.silthus.schat.platform.config.key.ConfigKeyFactory.lowercaseStringKey;
import static net.silthus.schat.platform.config.key.ConfigKeyFactory.modifiable;
import static net.silthus.schat.platform.config.key.ConfigKeyFactory.notReloadable;
import static net.silthus.schat.platform.locale.Messages.DisplayMode.TEXT;

@Log(topic = "sChat:config")
public final class ConfigKeys {

    public static final ConfigKey<String> MESSENGER = notReloadable(lowercaseStringKey("messenger", "pluginmessage"));
    public static final ConfigKey<Boolean> DEBUG = notReloadable(booleanKey("debug", false));
    public static final ConfigKey<Map<String, ChannelConfig>> CHANNELS = modifiable(key(config -> {
        final HashMap<String, ChannelConfig> channels = new HashMap<>();
        for (final String key : config.keys("channels", new ArrayList<>())) {
            channels.put(key, requireNonNullElse(config.get("channels." + key, ChannelConfig.class), new ChannelConfig()).key(key));
        }
        return channels;
    }), (c, value) -> {
        for (final ChannelConfig channel : value.values()) {
            c.set("channels." + channel.key(), channel);
        }
    });
    public static final ConfigKey<ViewConfig> VIEW_CONFIG = key(config -> config.get("view", ViewConfig.class));
    public static final ConfigKey<Map<String, Messages.DisplayMode>> MESSAGE_MODES = key(ConfigKeys::loadMessageModes);

    /**
     * A list of the keys defined in this class.
     */
    private static final List<? extends ConfigKey<?>> KEYS = KeyedConfiguration.initialise(ConfigKeys.class);

    @NotNull
    private static HashMap<String, Messages.DisplayMode> loadMessageModes(ConfigurationAdapter config) {
        final HashMap<String, Messages.DisplayMode> messageModes = new HashMap<>();
        for (String key : config.keys("message_modes", new ArrayList<>())) {
            final Messages.DisplayMode value = requireNonNullElse(config.get("message_modes." + key, Messages.DisplayMode.class), TEXT);
            for (Field field : Messages.class.getDeclaredFields()) {
                if (field.getName().equalsIgnoreCase(key)) {
                    try {
                        final Method method = field.getType().getDeclaredMethod("mode", Messages.DisplayMode.class);
                        method.invoke(field.get(null), value);
                        messageModes.put(key, value);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        log.warning(key + " is not a valid message type whose display mode can be modified.");
                    }
                }
            }
        }
        return messageModes;
    }

    private ConfigKeys() {
    }

    public static List<? extends ConfigKey<?>> keys() {
        return KEYS;
    }
}
