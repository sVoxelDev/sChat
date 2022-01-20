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
import net.silthus.schat.settings.Settings;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

class ConfigurateConfigSection implements ConfigurationSection {

    private final MiniMessage parser = MiniMessage.miniMessage();
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
            builder.setUnknown(key, setting -> scoped.get(key, setting.getType()));
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
