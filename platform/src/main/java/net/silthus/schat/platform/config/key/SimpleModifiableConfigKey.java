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

import java.util.function.BiConsumer;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;

final class SimpleModifiableConfigKey<T> extends SimpleConfigKey<T> implements ModifiableConfigKey<T> {

    private final BiConsumer<ConfigurationAdapter, T> setter;

    SimpleModifiableConfigKey(SimpleConfigKey<T> key, BiConsumer<ConfigurationAdapter, T> setter) {
        super(key.getFunction());
        this.setter = setter;
    }

    @Override
    public void set(ConfigurationAdapter adapter, T value) {
        this.setter.accept(adapter, value);
    }
}
