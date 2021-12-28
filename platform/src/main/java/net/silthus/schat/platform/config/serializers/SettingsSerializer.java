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

package net.silthus.schat.platform.config.serializers;

import java.lang.reflect.Type;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.settings.Setting;
import net.silthus.schat.settings.Settings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public final class SettingsSerializer implements TypeSerializer<Settings> {
    @Override
    public Settings deserialize(Type type, ConfigurationNode node) {
        final Settings.Builder builder = Settings.settings();
        for (final String key : node.childrenMap().keySet().stream().map(Object::toString).toList()) {
            builder.withUnknownType(key, setting -> {
                try {
                    return node.node(key).get(setting.getType());
                } catch (SerializationException e) {
                    throw new ConfigurationAdapter.LoadFailed(e);
                }
            });
        }
        return builder.create();
    }

    @Override
    public void serialize(Type type, @Nullable Settings settings, ConfigurationNode node) throws SerializationException {
        if (settings == null) return;
        for (final Setting<?> setting : settings.getSettings()) {
            node.node(setting.getKey()).set(setting.getType(), settings.get(setting));
        }
    }
}
