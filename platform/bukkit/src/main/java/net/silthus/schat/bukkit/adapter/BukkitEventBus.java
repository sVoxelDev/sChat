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

package net.silthus.schat.bukkit.adapter;

import net.silthus.schat.eventbus.AbstractEventBus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

public final class BukkitEventBus extends AbstractEventBus<Plugin> implements Listener {
    @Override
    protected Plugin checkPlugin(Object plugin) throws IllegalArgumentException {
        if (plugin instanceof Plugin p)
            return p;
        else
            throw new IllegalArgumentException();
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        unregisterHandlers(event.getPlugin());
    }
}
