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

package net.silthus.schat.platform.config.adapter;

import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.silthus.schat.pointer.Settings;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

class ConfigurateConfigSection implements ConfigurationSection {

    private final MiniMessage parser = MiniMessage.get();
    @Getter
    @Setter
    private ConfigurationNode root;

    ConfigurateConfigSection() {
    }

    private ConfigurateConfigSection(ConfigurationNode root) {
        this.root = root;
    }

    @Override
    public ConfigurateConfigSection scoped(String path) {
        return new ConfigurateConfigSection(resolvePath(path));
    }

    public <V> @Nullable V get(String path, Class<V> type) {
        try {
            return resolvePath(path).get(type);
        } catch (SerializationException e) {
            return null;
        }
    }

    @Override
    public void set(String path, Object value) {
        try {
            resolvePath(path).set(value);
        } catch (SerializationException e) {
            throw new ConfigurationAdapter.SaveFailed(e);
        }
    }

    public String getString(String path, String def) {
        return resolvePath(path).getString(def);
    }

    public int getInteger(String path, int def) {
        return resolvePath(path).getInt(def);
    }

    public boolean getBoolean(String path, boolean def) {
        return resolvePath(path).getBoolean(def);
    }

    public Component getParsedString(String path, Component def) {
        final String string = resolvePath(path).getString();
        if (string == null) return def;
        return parser.parse(string);
    }

    @Override
    public Settings getSettings(String path) {
        final Settings.Builder builder = Settings.settings();
        final ConfigurateConfigSection scoped = scoped(path);
        for (final String key : getKeys(path, new ArrayList<>())) {
            builder.withUnknown(key, setting -> scoped.get(key, setting.getType()));
        }
        return builder.create();
    }

    @SneakyThrows
    public List<String> getStringList(String path, List<String> def) {
        ConfigurationNode node = resolvePath(path);
        if (node.virtual() || !node.isList()) {
            return def;
        }

        return node.getList(String.class);
    }

    public List<String> getKeys(String path, List<String> def) {
        ConfigurationNode node = resolvePath(path);
        if (node.virtual() || !node.isMap()) {
            return def;
        }

        return node.childrenMap().keySet().stream().map(Object::toString).collect(Collectors.toList());
    }

    @SuppressWarnings("UnstableApiUsage")
    private ConfigurationNode resolvePath(String path) {
        if (path == null || path.isBlank()) return getRoot();

        return getRoot().node(Splitter.on('.').splitToList(path).toArray());
    }
}
