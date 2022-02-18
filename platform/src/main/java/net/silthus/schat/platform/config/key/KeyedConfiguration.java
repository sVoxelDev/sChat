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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import net.silthus.schat.platform.config.Config;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;

public class KeyedConfiguration implements Config {

    /**
     * Initialises the given pseudo-enum keys class.
     *
     * @param keysClass the keys class
     * @return the list of keys defined by the class with their ordinal values set
     */
    public static List<SimpleConfigKey<?>> initialise(Class<?> keysClass) {
        List<SimpleConfigKey<?>> keys = allConfigKeysFromClass(keysClass);
        setOrdinalValues(keys);
        return keys;
    }

    @SuppressWarnings("SimplifyStreamApiCallChains")
    private static List<SimpleConfigKey<?>> allConfigKeysFromClass(Class<?> keysClass) {
        return Arrays.stream(keysClass.getFields())
            .filter(KeyedConfiguration::isStatic)
            .filter(KeyedConfiguration::isConfigKey)
            .map(KeyedConfiguration::configKey)
            .collect(Collectors.toUnmodifiableList());
    }

    private static boolean isStatic(Field f) {
        return Modifier.isStatic(f.getModifiers());
    }

    private static boolean isConfigKey(Field f) {
        return ConfigKey.class.equals(f.getType());
    }

    @SneakyThrows
    private static SimpleConfigKey<?> configKey(Field f) {
        return (SimpleConfigKey<?>) f.get(null);
    }

    private static void setOrdinalValues(List<SimpleConfigKey<?>> keys) {
        for (int i = 0; i < keys.size(); i++) {
            keys.get(i).setOrdinal(i);
        }
    }

    private final ConfigurationAdapter adapter;
    private final List<? extends ConfigKey<?>> keys;
    private final ValuesMap values;

    public KeyedConfiguration(ConfigurationAdapter adapter, List<? extends ConfigKey<?>> keys) {
        this.adapter = adapter;
        this.keys = keys;
        this.values = new ValuesMap(keys.size());
    }

    protected void init() {
        load(configKey -> true);
    }

    @Override
    public void save() {
        for (final ConfigKey<?> key : this.keys) {
            if (key instanceof ModifiableConfigKey<?> modifiable)
                saveConfigKey(modifiable);

        }
        this.adapter.save();
    }

    @Override
    public void load() {
        load(ConfigKey::reloadable);
    }

    /**
     * Reloads the configuration.
     */
    @Override
    public void reload() {
        load();
    }

    protected void load(Predicate<ConfigKey<?>> filter) {
        this.adapter.load();
        for (ConfigKey<?> key : this.keys) {
            if (filter.test(key)) {
                this.values.put(key, key.get(this.adapter));
            }
        }
    }

    /**
     * Gets the value of a given context key.
     *
     * @param key the key
     * @param <T> the key return type
     * @return the value mapped to the given key. May be null.
     */
    @Override
    public <T> T get(ConfigKey<T> key) {
        return this.values.get(key);
    }

    @Override
    public <T> void set(ConfigKey<T> key, T value) {
        if (key instanceof ModifiableConfigKey<T> modifiable)
            modifiable.set(this.adapter, value);
    }

    private <T> void saveConfigKey(ModifiableConfigKey<T> key) {
        key.set(this.adapter, key.get(this.adapter));
    }

    private static final class ValuesMap {
        private final Object[] values;

        ValuesMap(int size) {
            this.values = new Object[size];
        }

        @SuppressWarnings("unchecked")
        public <T> T get(ConfigKey<T> key) {
            return (T) this.values[key.ordinal()];
        }

        public void put(ConfigKey<?> key, Object value) {
            this.values[key.ordinal()] = value;
        }
    }
}
