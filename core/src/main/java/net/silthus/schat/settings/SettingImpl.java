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

package net.silthus.schat.settings;

import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode(of = {"type", "key"})
final class SettingImpl<V> implements Setting<V> {

    private final Class<V> type;
    private final String key;
    private final Supplier<V> defaultValue;

    SettingImpl(
        final @NonNull Class<V> type,
        final @NonNull String key,
        final @NonNull Supplier<V> defaultValue) {
        this.type = type;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public V getDefaultValue() {
        return this.defaultValue.get();
    }
}
