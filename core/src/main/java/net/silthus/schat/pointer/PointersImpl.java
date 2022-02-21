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
package net.silthus.schat.pointer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

non-sealed class PointersImpl implements Pointers {
    static final Pointers EMPTY = new EmptyPointers();

    protected final Map<Pointer<?>, Supplier<?>> pointers;

    protected PointersImpl(final @NonNull Map<Pointer<?>, Supplier<?>> pointers) {
        this.pointers = pointers;
    }

    @Override
    public @NotNull Set<Pointer<?>> pointers() {
        return Collections.unmodifiableSet(this.pointers.keySet());
    }

    @Override
    @SuppressWarnings("unchecked") // all values are checked on entry
    public @NotNull <T> Optional<T> get(final @NonNull Pointer<T> pointer) {
        final Supplier<?> supplier = this.pointers.get(pointer);
        if (supplier == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable((T) supplier.get());
        }
    }

    @Override
    public <T> boolean contains(final @NonNull Pointer<T> pointer) {
        return this.pointers.containsKey(pointer);
    }

    @Override
    public @NotNull Pointers.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static final class BuilderImpl implements Builder {
        private final Map<Pointer<?>, Supplier<?>> pointers;

        BuilderImpl() {
            this.pointers = new HashMap<>();
        }

        BuilderImpl(final @NotNull PointersImpl pointers) {
            this.pointers = new HashMap<>(pointers.pointers);
        }

        @Override
        public @NotNull <T> Builder withDynamic(final @NonNull Pointer<T> pointer, final @NonNull Supplier<@Nullable T> value) {
            this.pointers.put(pointer, value);
            return this;
        }

        @Override
        public @NotNull Pointers create() {
            return new PointersImpl(pointers);
        }
    }

    static final class EmptyPointers implements Pointers {
        @Override
        public @NotNull Set<Pointer<?>> pointers() {
            return Collections.emptySet();
        }

        @Override
        public @NotNull <T> Optional<T> get(final @NonNull Pointer<T> pointer) {
            return Optional.empty();
        }

        @Override
        public <T> boolean contains(final @NonNull Pointer<T> pointer) {
            return false;
        }

        @Override
        public @NotNull Builder toBuilder() {
            return new BuilderImpl();
        }

        @Override
        public String toString() {
            return "EmptyPointers";
        }
    }
}
