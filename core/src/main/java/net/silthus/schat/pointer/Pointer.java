/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2021 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.silthus.schat.pointer;

import java.lang.ref.WeakReference;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A pointer to a resource.
 *
 * @param <V> the value type
 * @since next
 */
public sealed interface Pointer<V> permits PointerImpl, Setting {
    /**
     * Creates a pointer.
     *
     * @param type the value type
     * @param key  the key
     * @param <V>  the value type
     * @return the pointer
     * @since next
     */
    static <V> @NotNull Pointer<V> pointer(final @NonNull Class<V> type, final @NonNull String key) {
        return new PointerImpl<>(type, key);
    }

    /**
     * Creates a forward pointer to a pointer of another pointered resource.
     *
     * <p>The forwarder returns a null value of the pointer does not exist or its value is null in the target.</p>
     *
     * @param pointered     the forward target
     * @param targetPointer the pointer on the forward target
     * @param <T>           the value type of the pointer
     * @return a supplier that forwards to the given pointer
     * @since next
     */
    static <T> @NotNull Supplier<T> forward(@NonNull final Pointered pointered, @NonNull final Pointer<T> targetPointer) {
        return forwardWithDefault(pointered, targetPointer, null);
    }

    /**
     * Creates a forward pointer with a default value to a pointer of another pointered resource.
     *
     * <p>The forwarder returns the default value if the pointer does not exist in the target.</p>
     *
     * @param pointered     the forward target
     * @param targetPointer the pointer on the forward target
     * @param defaultValue  the default value to use if the target pointer does not exist
     * @param <T>           the value type of the pointer
     * @return a supplier that forwards to the given pointer
     * @since next
     */
    static <T> @NotNull Supplier<T> forwardWithDefault(@NonNull final Pointered pointered, @NonNull final Pointer<T> targetPointer, @Nullable final T defaultValue) {
        return () -> pointered.getOrDefault(targetPointer, defaultValue);
    }

    /**
     * Creates a dynamic pointer that will hold a weak reference to the given type required to resolve the pointer.
     *
     * @param type the type used to resolve the pointer
     * @param value the resolver of the pointer
     * @param <V> the value type
     * @param <T> the reference type
     * @return the pointer supplier
     */
    static <V, T> @NotNull Supplier<V> weak(@NonNull T type, @NonNull Function<T, V> value) {
        return weak(type, value, null);
    }

    /**
     * Creates a dynamic pointer that will hold a weak reference to the given type required to resolve the pointer.
     *
     * @param type the type used to resolve the pointer
     * @param value the resolver of the pointer
     * @param fallbackValue the value to use if the reference is expired
     * @param <V> the value type
     * @param <T> the reference type
     * @return the pointer supplier
     */
    static <V, T> @NotNull Supplier<V> weak(@NonNull T type, @NonNull Function<T, V> value, @Nullable V fallbackValue) {
        final WeakReference<T> reference = new WeakReference<>(type);
        return () -> {
            final T t = reference.get();
            if (t != null)
                return value.apply(t);
            else
                return fallbackValue;
        };
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
}
