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

import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Something that can retrieve values based on a given {@link Pointer}.
 *
 * @since next
 */
public interface Pointered {
    /**
     * Gets the value of {@code pointer}.
     *
     * @param pointer the pointer
     * @param <T>     the type
     * @return the value
     * @since next
     */
    default <T> @NotNull Optional<T> get(final @NotNull Pointer<T> pointer) {
        return this.getPointers().get(pointer);
    }

    /**
     * Resolves the value of a boolean pointer. Defaults to false if the pointer is not supported.
     *
     * @param pointer the pointer to resolve
     * @return the value of the pointer. false if it does not exist.
     */
    default boolean is(Pointer<Boolean> pointer) {
        return getOrDefault(pointer, false);
    }

    /**
     * Gets the pointers for this object.
     *
     * @return the pointers
     * @since next
     */
    default @NotNull Pointers getPointers() {
        return Pointers.empty();
    }

    /**
     * Gets the value of {@code pointer}.
     *
     * <p>If this {@code Audience} is unable to provide a value for {@code pointer}, {@code defaultValue} will be returned.</p>
     *
     * @param pointer      the pointer
     * @param defaultValue the default value
     * @param <T>          the type
     * @return the value
     * @since next
     */
    @Contract("_, null -> _; _, !null -> !null")
    @SuppressWarnings("checkstyle:MethodName")
    default <T> @Nullable T getOrDefault(final @NotNull Pointer<T> pointer, final @Nullable T defaultValue) {
        return this.getPointers().getOrDefault(pointer, defaultValue);
    }

    /**
     * Gets the value of {@code pointer}.
     *
     * <p>If this {@code Audience} is unable to provide a value for {@code pointer}, the value supplied by {@code defaultValue} will be returned.</p>
     *
     * @param pointer      the pointer
     * @param defaultValue the default value supplier
     * @param <T>          the type
     * @return the value
     * @since next
     */
    @SuppressWarnings("checkstyle:MethodName")
    default <T> @UnknownNullability T getOrDefaultFrom(final @NotNull Pointer<T> pointer, final @NotNull Supplier<? extends T> defaultValue) {
        return this.getPointers().getOrDefaultFrom(pointer, defaultValue);
    }
}
