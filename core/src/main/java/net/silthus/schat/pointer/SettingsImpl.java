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
    public @NotNull Set<Setting<?>> getSettings() {
        final HashSet<Setting<?>> settings = new HashSet<>();
        for (Pointer<?> pointer : pointers.keySet()) {
            if (pointer instanceof Setting<?> setting)
                settings.add(setting);
        }
        return Collections.unmodifiableSet(settings);
    }

    @Override
    public @NotNull <V> V get(final @NonNull Setting<V> setting) {
        return this.valueFromSupplier(setting, getValueSupplier(setting));
    }

    @Override
    public <V> @UnknownNullability V getOrDefaultFrom(@NotNull Setting<V> setting, @NotNull Supplier<? extends V> defaultValue) {
        final Supplier<?> supplier = getValueSupplier(setting);
        return supplier == null ? defaultValue.get() : this.valueFromSupplier(setting, supplier);
    }

    @Override
    public @NotNull <V> Optional<V> set(final @NonNull Setting<V> setting, final @Nullable V value) {
        return Optional.ofNullable(this.valueFromSupplier(setting, this.pointers.put(setting, () -> value)));
    }

    @SuppressWarnings("unchecked") // all values are checked on entry
    private <V> V valueFromSupplier(final @NonNull Setting<V> setting, final @Nullable Supplier<?> supplier) {
        if (supplier == null) {
            return setting.getDefaultValue();
        } else {
            return Optional.ofNullable((V) supplier.get())
                .orElse(setting.getDefaultValue());
        }
    }

    private <V> Supplier<?> getValueSupplier(@NotNull Setting<V> setting) {
        return matchesUnknownValue(setting) ? getUnknownValue(setting) : getConfiguredValue(setting);
    }

    private <V> Supplier<?> getConfiguredValue(@NotNull Setting<V> setting) {
        return this.pointers.get(setting);
    }

    @NotNull
    private <V> Supplier<Object> getUnknownValue(@NotNull Setting<V> setting) {
        return () -> unknowns.get(setting.getKey()).apply(setting);
    }

    private <V> boolean notContains(@NotNull Setting<V> setting) {
        return !supports(setting);
    }

    private <V> boolean matchesUnknownValue(@NotNull Setting<V> setting) {
        return notContains(setting) && isUnknownKey(setting.getKey());
    }

    private boolean isUnknownKey(String key) {
        return unknowns.containsKey(key);
    }

    @Override
    public @NotNull Settings.Builder copy() {
        return new BuilderImpl(this);
    }

    @Override
    public String toString() {
        final HashMap<String, Object> keyValueMap = new HashMap<>();
        for (final Setting<?> setting : getSettings()) {
            keyValueMap.put(setting.getKey(), get(setting));
        }
        return "SettingsImpl{" + keyValueMap + '}';
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
