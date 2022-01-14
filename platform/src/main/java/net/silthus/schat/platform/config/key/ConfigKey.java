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

import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;

/**
 * Represents a key in the configuration.
 *
 * @param <T> the value type
 */
public interface ConfigKey<T> {

    /**
     * Gets the position of this key within the keys enum.
     *
     * @return the position
     */
    int ordinal();

    /**
     * Gets if the config key can be reloaded.
     *
     * @return the if the key can be reloaded
     */
    boolean reloadable();

    /**
     * Resolves and returns the value mapped to this key using the given config instance.
     *
     * @param adapter the config adapter instance
     * @return the value mapped to this key
     */
    T get(ConfigurationAdapter adapter);
}
