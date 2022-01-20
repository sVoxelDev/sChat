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

package net.silthus.schat.pointer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;

/**
 * A collection of {@link Setting settings}.
 *
 * @since next
 */
public sealed interface Settings extends Pointers permits SettingsImpl {
    /**
     * Gets an empty settings collection.
     *
     * @return the settings
     * @since next
     */
    static @NotNull Settings createSettings() {
        return settings().create();
    }

    /**
     * Gets a new settings builder.
     *
     * @return the builder
     * @see Builder
     * @since next
     */
    static @NotNull Builder settings() {
        return new SettingsImpl.BuilderImpl();
    }

    /**
     * Gets all settings in this collection which have the given value type.
     *
     * @param valueType the type class of the setting
     * @param <V>       the type of the setting
     * @return an unmodifiable set of settings filtered by the given type
     * @since next
     */
    @SuppressWarnings("unchecked")
    default <V> @NotNull @Unmodifiable Set<Setting<V>> getSettings(final @NonNull Class<V> valueType) {
        final Set<Setting<V>> filteredSettings = new HashSet<>();
        for (final Setting<?> setting : this.getSettings()) {
            if (valueType.isAssignableFrom(setting.getType()))
                filteredSettings.add((Setting<V>) setting);
        }
        return Collections.unmodifiableSet(filteredSettings);
    }

    /**
     * Gets all settings in this settings collection.
     *
     * @return an unmodifiable set of settings in this settings collection
     * @throws UnsupportedOperationException if the implementing class does not support querying for settings
     * @since next
     */
    @NotNull @Unmodifiable Set<Setting<?>> getSettings();

    /**
     * Gets the value of {@code setting}.
     *
     * <p>Will use the {@link Setting#getDefaultValue()} if the setting does not exist.</p>
     *
     * @param setting the setting
     * @param <V>     the type
     * @return the value
     * @since next
     */
    <V> @NotNull V get(final @NotNull Setting<V> setting);

    /**
     * Gets the value of {@code setting}.
     *
     * <p>If a value for {@code setting} is unable to be provided, {@code defaultValue} will be returned.</p>
     *
     * @param setting      the setting
     * @param defaultValue the default value
     * @param <V>          the type
     * @return the value
     * @since next
     */
    default <V> @Nullable V getOrDefault(final @NotNull Setting<V> setting, final @Nullable V defaultValue) {
        return getOrDefaultFrom(setting, () -> defaultValue);
    }

    /**
     * Gets the value of {@code setting}.
     *
     * <p>If a value for {@code setting} is unable to be provided, the value supplied by {@code defaultValue} will be returned.</p>
     *
     * @param setting      the setting
     * @param defaultValue the default value supplier
     * @param <V>          the type
     * @return the value
     * @since next
     */
    <V> @UnknownNullability V getOrDefaultFrom(final @NotNull Setting<V> setting, final @NotNull Supplier<? extends V> defaultValue);

    /**
     * Sets the value of the setting of the setting.
     *
     * @param setting the setting
     * @param value   the new value of the setting
     * @param <V>     the type of the value
     * @return the old value if replaced
     * @since next
     */
    <V> @NotNull Optional<V> set(@NotNull Setting<V> setting, @Nullable V value);

    Builder copy();

    /**
     * A builder of settings.
     *
     * @see Settings
     * @since next
     */
    interface Builder extends Pointers.Builder {

        /**
         * Adds a setting which type is unknown and will be resolved based on the given key.
         *
         * @param key the key of the setting matched against the alias or keys of configured settings
         * @param value the value that will be resolved by the setting
         * @param <V> the type of the value
         * @return this builder
         * @since next
         */
        <V> @NotNull Builder withUnknown(final @NotNull String key, @NotNull Function<Setting<?>, V> value);

        @Override
        default <T> @NotNull Builder withStatic(final @NonNull Pointer<T> pointer, @Nullable final T value) {
            Pointers.Builder.super.withStatic(pointer, value);
            return this;
        }

        @Override
        <T> @NotNull Builder withDynamic(final @NonNull Pointer<T> pointer, @NonNull Supplier<@Nullable T> value);

        @Override
        default <T> @NotNull Builder withForward(final @NonNull Pointer<T> pointer, final @NonNull Pointered target, final @NonNull Pointer<T> targetPointer) {
            Pointers.Builder.super.withForward(pointer, target, targetPointer);
            return this;
        }

        Settings create();
    }
}
