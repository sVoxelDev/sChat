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

package net.silthus.schat.platform.config.key;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;

public interface ConfigKeyFactory<T> {

    ConfigKeyFactory<Boolean> BOOLEAN = ConfigurationAdapter::getBoolean;
    ConfigKeyFactory<String> STRING = ConfigurationAdapter::getString;
    ConfigKeyFactory<String> LOWERCASE_STRING = (adapter, path, def) -> adapter.getString(path, def).toLowerCase(Locale.ROOT);

    static <T> SimpleConfigKey<T> key(Function<ConfigurationAdapter, T> function) {
        return new SimpleConfigKey<>(function);
    }

    static <T> SimpleModifiableConfigKey<T> modifiable(SimpleConfigKey<T> key, BiConsumer<ConfigurationAdapter, T> setter) {
        return new SimpleModifiableConfigKey<>(key, setter);
    }

    static <T> SimpleConfigKey<T> notReloadable(SimpleConfigKey<T> key) {
        key.setReloadable(false);
        return key;
    }

    static SimpleConfigKey<Boolean> booleanKey(String path, boolean def) {
        return key(new Bound<>(BOOLEAN, path, def));
    }

    static SimpleConfigKey<String> stringKey(String path, String def) {
        return key(new Bound<>(STRING, path, def));
    }

    static SimpleConfigKey<String> lowercaseStringKey(String path, String def) {
        return key(new Bound<>(LOWERCASE_STRING, path, def));
    }

    /**
     * Extracts the value from the config.
     *
     * @param config the config
     * @param path the path where the value is
     * @param def the default value
     * @return the value
     */
    T getValue(ConfigurationAdapter config, String path, T def);

    /**
     * A {@link ConfigKeyFactory} bound to a given {@code path}.
     *
     * @param <T> the value type
     */
    class Bound<T> implements Function<ConfigurationAdapter, T> {
        private final ConfigKeyFactory<T> factory;
        private final String path;
        private final T def;

        Bound(ConfigKeyFactory<T> factory, String path, T def) {
            this.factory = factory;
            this.path = path;
            this.def = def;
        }

        @Override
        public T apply(ConfigurationAdapter adapter) {
            return this.factory.getValue(adapter, this.path, this.def);
        }
    }

}
