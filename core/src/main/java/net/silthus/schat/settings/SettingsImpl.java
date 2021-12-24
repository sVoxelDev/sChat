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
import java.util.function.Supplier;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

final class SettingsImpl implements Settings {

    private final Map<Setting<?>, Supplier<?>> settings;

    SettingsImpl(final @NotNull SettingsImpl.BuilderImpl builder) {
        this.settings = new HashMap<>(builder.settings);
    }

    @Override
    public @NotNull Set<Setting<?>> getSettings() {
        return Collections.unmodifiableSet(settings.keySet());
    }

    @Override
    public @NotNull <V> V get(final @NonNull Setting<V> setting) {
        final Supplier<?> supplier = this.settings.get(setting);
        return this.valueFromSupplier(setting, supplier);
    }

    @Override
    public <V> @UnknownNullability V getOrDefaultFrom(@NotNull Setting<V> setting, @NotNull Supplier<? extends V> defaultValue) {
        final Supplier<?> supplier = this.settings.get(setting);
        if (supplier == null)
            return defaultValue.get();
        return this.valueFromSupplier(setting, supplier);
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
    public <T> boolean supports(final @NonNull Setting<T> setting) {
        return this.settings.containsKey(setting);
    }

    @Override
    public @NotNull Settings.Builder copy() {
        return new BuilderImpl(this);
    }

    static final class BuilderImpl implements Builder {

        private final Map<Setting<?>, Supplier<?>> settings;

        BuilderImpl() {
            this.settings = new HashMap<>();
        }

        BuilderImpl(final @NotNull SettingsImpl settings) {
            this.settings = new HashMap<>(settings.settings);
        }

        @Override
        public @NotNull <T> Settings.Builder withDynamic(final @NonNull Setting<T> setting, final @NonNull Supplier<@Nullable T> value) {
            this.settings.put(setting, value);
            return this;
        }

        @Override
        public @NotNull Settings create() {
            return new SettingsImpl(this);
        }
    }
}
