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
import net.kyori.adventure.text.Component;
import net.silthus.schat.settings.Settings;
import net.silthus.schat.usecases.ChannelConfig;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public final class ChannelConfigSerializer implements TypeSerializer<ChannelConfig> {
    @Override
    public ChannelConfig deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final ChannelConfig config = new ChannelConfig();
        config.setName(node.node("name").get(Component.class));
        config.setSettings(node.node("settings").get(Settings.class));
        return config;
    }

    @Override
    public void serialize(Type type, @Nullable ChannelConfig obj, ConfigurationNode node) throws SerializationException {
        if (obj == null)
            return;
        node.node("name").set(Component.class, obj.getName());
        node.node("settings").set(Settings.class, obj.getSettings());
    }
}
