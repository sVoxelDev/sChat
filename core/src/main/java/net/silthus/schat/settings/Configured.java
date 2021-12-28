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

import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Something that can retrieve a {@link Setting} from {@link Settings}.
 *
 * @since next
 */
public interface Configured {

    /**
     * Gets the value of {@code setting} or the setting's default value if the value is not present.
     *
     * @param setting the setting
     * @param <V>     the type
     * @return the value
     * @since next
     */
    default <V> @NotNull V get(final @NonNull Setting<V> setting) {
        return this.getSettings().get(setting);
    }

    /**
     * Gets the value of {@code setting}.
     *
     * <p>If this configured object is unable to provide a value for {@code setting}, the {@code defaultValue} will be returned.</p>
     *
     * @param setting      the setting
     * @param defaultValue the default value
     * @param <V>          the type
     * @return the value
     * @since next
     */
    default <V> @UnknownNullability V getOrDefault(final @NonNull Setting<V> setting, final @Nullable V defaultValue) {
        return this.getSettings().getOrDefault(setting, defaultValue);
    }

    /**
     * Gets the value of {@code setting}.
     *
     * <p>If this configured object is unable to provide a value for {@code setting}, the value supplied by {@code defaultValue} will be returned.</p>
     *
     * @param setting      the setting
     * @param defaultValue the default value supplier
     * @param <V>          the type
     * @return the value
     * @since next
     */
    default <V> @UnknownNullability V getOrDefaultFrom(final @NonNull Setting<V> setting, final @NonNull Supplier<? extends V> defaultValue) {
        return this.getSettings().getOrDefaultFrom(setting, defaultValue);
    }

    /**
     * Gets the settings for this object.
     *
     * @return the settings
     * @since next
     */
    default @NotNull Settings getSettings() {
        return Settings.createSettings();
    }

    interface Builder<T> {

        /**
         * Sets a setting of the channel to the given value.
         *
         * @param setting the setting
         * @param value   the value of the setting
         * @param <V>     the type of the setting
         * @return this builder
         * @since next
         */
        <V> @NotNull T setting(@NonNull Setting<V> setting, @Nullable V value);

        /**
         * Sets the settings container to use for this channel.
         *
         * <p>By default an {@link Settings#createSettings()} container is used and can be populated
         * during the builder stage or afterwards.</p>
         *
         * <p>Using this builder method after settings have been set will reset those settings and replace them with the new container.
         * All subsequent calls to set settings will be directed to the new settings container.</p>
         *
         * @param settings the settings container to use
         * @return this builder
         * @since next
         */
        @NotNull T settings(@NotNull Settings settings);

        /**
         * Sets settings in the existing settings container.
         *
         * <p>Setting settings this way is additive and uses the default or explicit settings container.</p>
         *
         * @param settings the settings
         * @return this builder
         * @since next
         */
        @NotNull T settings(@NotNull Consumer<Settings.Builder> settings);
    }
}
