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
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

final class SettingsImpl extends PointersImpl implements Settings {

    private final Map<String, Function<Setting<?>, ?>> unknowns;

    SettingsImpl(final @NotNull SettingsImpl.BuilderImpl builder) {
        super(builder.pointers);
        this.unknowns = new HashMap<>(builder.unknowns);
    }

    @Override
    public @NotNull Set<Setting<?>> settings() {
        final HashSet<Setting<?>> settings = new HashSet<>();
        for (Pointer<?> pointer : pointers.keySet()) {
            if (pointer instanceof Setting<?> setting)
                settings.add(setting);
        }
        return Collections.unmodifiableSet(settings);
    }

    @Override
    public @UnknownNullability <V> V get(final @NonNull Setting<V> setting) {
        return this.valueFromSupplier(setting, valueSupplier(setting));
    }

    @Override
    public <V> @UnknownNullability V getOrDefaultFrom(@NotNull Setting<V> setting, @NotNull Supplier<? extends V> defaultValue) {
        final Supplier<?> supplier = valueSupplier(setting);
        return supplier == null ? defaultValue.get() : this.valueFromSupplier(setting, supplier);
    }

    @Override
    public @NotNull <V> Optional<V> set(final @NonNull Setting<V> setting, final @Nullable V value) {
        return Optional.ofNullable(this.valueFromSupplier(setting, this.pointers.put(setting, () -> value)));
    }

    @SuppressWarnings("unchecked") // all values are checked on entry
    private <V> V valueFromSupplier(final @NonNull Setting<V> setting, final @Nullable Supplier<?> supplier) {
        if (supplier == null) {
            return setting.defaultValue();
        } else {
            return Optional.ofNullable((V) supplier.get())
                .orElse(setting.defaultValue());
        }
    }

    private <V> Supplier<?> valueSupplier(@NotNull Setting<V> setting) {
        return matchesUnknownValue(setting) ? unknownValue(setting) : configuredValue(setting);
    }

    private <V> Supplier<?> configuredValue(@NotNull Setting<V> setting) {
        return this.pointers.get(setting);
    }

    @NotNull
    private <V> Supplier<Object> unknownValue(@NotNull Setting<V> setting) {
        return () -> unknowns.get(setting.key()).apply(setting);
    }

    private <V> boolean matchesUnknownValue(@NotNull Setting<V> setting) {
        return !super.contains(setting) && isUnknownKey(setting.key());
    }

    private boolean isUnknownKey(String key) {
        return unknowns.containsKey(key);
    }

    @Override
    public <T> boolean contains(@NonNull Pointer<T> pointer) {
        return super.contains(pointer) || isUnknownKey(pointer.key());
    }

    @Override
    public @NotNull Settings.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    @Override
    public @NotNull SettingsImpl copyFrom(Settings settings) {
        SettingsImpl s = (SettingsImpl) settings;
        unknowns.putAll(s.unknowns);
        pointers.putAll(s.pointers);
        return this;
    }

    @Override
    public String toString() {
        final HashMap<String, Object> keyValueMap = new HashMap<>();
        for (final Setting<?> setting : settings()) {
            keyValueMap.put(setting.key(), get(setting));
        }
        return "Settings{" + keyValueMap + '}';
    }

    static final class BuilderImpl implements Settings.Builder {
        private final Map<Pointer<?>, Supplier<?>> pointers;
        private final Map<String, Function<Setting<?>, ?>> unknowns;

        BuilderImpl() {
            this.pointers = new HashMap<>();
            this.unknowns = new HashMap<>();
        }

        BuilderImpl(final @NotNull SettingsImpl settings) {
            this.pointers = new HashMap<>(settings.pointers);
            this.unknowns = new HashMap<>(settings.unknowns);
        }

        @Override
        public @NotNull <V> Settings.Builder withUnknown(@NonNull String key, @NonNull Function<Setting<?>, V> value) {
            this.unknowns.putIfAbsent(key, value);
            return this;
        }

        @Override
        public @NotNull <V> Settings.Builder withDynamic(@NonNull Pointer<V> pointer, @NonNull Supplier<@Nullable V> value) {
            this.pointers.put(pointer, value);
            return this;
        }

        @Override
        public @NotNull Settings create() {
            return new SettingsImpl(this);
        }
    }
}
