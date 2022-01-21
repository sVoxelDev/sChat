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
