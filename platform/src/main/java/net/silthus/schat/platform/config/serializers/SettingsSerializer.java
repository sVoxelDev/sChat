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

package net.silthus.schat.platform.config.serializers;

import java.lang.reflect.Type;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.pointer.Settings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public final class SettingsSerializer implements TypeSerializer<Settings> {
    @Override
    public Settings deserialize(Type type, ConfigurationNode node) {
        final Settings.Builder builder = Settings.settings();
        for (final String key : node.childrenMap().keySet().stream().map(Object::toString).toList()) {
            builder.withUnknown(key, setting -> {
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
