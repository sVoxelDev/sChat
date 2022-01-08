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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

@EqualsAndHashCode(of = {"settings"})
final class SettingsImpl implements Settings {

    private final Map<Setting<?>, Supplier<?>> settings;
    private final Map<String, Function<Setting<?>, ?>> unknowns;

    SettingsImpl(final @NotNull SettingsImpl.BuilderImpl builder) {
        this.settings = new HashMap<>(builder.settings);
        this.unknowns = new HashMap<>(builder.unknowns);
    }

    @Override
    public @NotNull Set<Setting<?>> getSettings() {
        return Collections.unmodifiableSet(settings.keySet());
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
        return Optional.ofNullable(this.valueFromSupplier(setting, this.settings.put(setting, () -> value)));
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

    @Override
    public <T> boolean contains(final @NonNull Setting<T> setting) {
        return this.settings.containsKey(setting);
    }

    private <V> Supplier<?> getValueSupplier(@NotNull Setting<V> setting) {
        return matchesUnknownValue(setting) ? getUnknownValue(setting) : getConfiguredValue(setting);
    }

    private <V> Supplier<?> getConfiguredValue(@NotNull Setting<V> setting) {
        return this.settings.get(setting);
    }

    @NotNull
    private <V> Supplier<Object> getUnknownValue(@NotNull Setting<V> setting) {
        return () -> unknowns.get(setting.getKey()).apply(setting);
    }

    private <V> boolean notContains(@NotNull Setting<V> setting) {
        return !contains(setting);
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

    static final class BuilderImpl implements Builder {

        private final Map<Setting<?>, Supplier<?>> settings;
        private final Map<String, Function<Setting<?>, ?>> unknowns;

        BuilderImpl() {
            this.settings = new HashMap<>();
            this.unknowns = new HashMap<>();
        }

        BuilderImpl(final @NotNull SettingsImpl settings) {
            this.settings = new HashMap<>(settings.settings);
            this.unknowns = new HashMap<>(settings.unknowns);
        }

        @Override
        public @NotNull <T> Settings.Builder withDynamic(final @NonNull Setting<T> setting, final @NonNull Supplier<@Nullable T> value) {
            this.settings.put(setting, value);
            return this;
        }

        @Override
        public @NotNull <V> Builder withUnknownType(@NonNull String key, @NonNull Function<Setting<?>, V> value) {
            this.unknowns.putIfAbsent(key, value);
            return this;
        }

        @Override
        public @NotNull Settings create() {
            return new SettingsImpl(this);
        }
    }
}
