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

package net.silthus.schat.platform.config;

import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.events.config.ConfigReloadedEvent;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.key.KeyedConfiguration;

public final class SChatConfig extends KeyedConfiguration {

    private final EventBus eventBus;

    public SChatConfig(ConfigurationAdapter adapter, EventBus eventBus) {
        super(adapter, ConfigKeys.getKeys());
        this.eventBus = eventBus;

        init();
    }

    public SChatConfig(ConfigurationAdapter adapter) {
        this(adapter, EventBus.empty());
    }

    @Override
    public void reload() {
        super.reload();
        eventBus.post(new ConfigReloadedEvent());
    }
}
