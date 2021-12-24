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
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A setting of a resource with an optional default value.
 *
 * @param <V> the type of the value
 * @since next
 */
public interface Setting<V> {

    /**
     * Creates a new setting for the given type and key with the given default value.
     *
     * @param type         the type of the setting value
     * @param key          the key of the setting
     * @param defaultValue the default value to use if the setting is not set
     * @param <V>          the type of the value
     * @return the setting
     * @since next
     */
    static @NotNull <V> Setting<V> setting(final @NonNull Class<V> type, final @NonNull String key, final @Nullable V defaultValue) {
        return dynamicSetting(type, key, () -> defaultValue);
    }

    /**
     * Creates a new setting for the given type and key with the given dynamic default value supplier.
     *
     * @param type         the type of the setting value
     * @param key          the key of the setting
     * @param defaultValue the default value to use if the setting is not set
     * @param <V>          the type of the value
     * @return the setting
     * @since next
     */
    static @NotNull <V> Setting<V> dynamicSetting(final @NonNull Class<V> type, final @NonNull String key, final @NonNull Supplier<@Nullable V> defaultValue) {
        return new SettingImpl<>(type, key, defaultValue);
    }

    /**
     * Gets the value type.
     *
     * @return the value type
     * @since next
     */
    @NotNull Class<V> getType();

    /**
     * Gets the key.
     *
     * @return the key
     * @since next
     */
    @NotNull String getKey();

    /**
     * The default value of the setting that is used if the setting is not set.
     *
     * @return the default value
     * @since next
     */
    @Nullable V getDefaultValue();

    /**
     * Creates a new setting from this setting with the new default value.
     *
     * @param defaultValue the new default value
     * @return the copied setting with the new default value
     * @since next
     */
    @NotNull Setting<V> withDefaultValue(final @Nullable V defaultValue);
}
