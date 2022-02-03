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

package net.silthus.schat.bungeecord.adapter;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.silthus.schat.eventbus.AbstractEventBus;

public class BungeecordEventBus extends AbstractEventBus<Plugin> implements Listener {
    private final ProxyServer proxy;

    public BungeecordEventBus(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    protected Plugin checkPlugin(Object plugin) throws IllegalArgumentException {
        if (plugin instanceof Plugin bungeePlugin) {
            // add a custom log handler to effectively listen for the plugin being disabled.
            // BungeeCord doesn't really support enabling/disabling plugins at runtime, and as
            // such doesn't have a PluginDisableEvent. However, some plugins do exist to reload
            // plugins at runtime. We rely on these plugins following the BungeeCord behaviour,
            // and #close ing the plugins logger, so we can unregister the listeners. :)
            Handler[] handlers = bungeePlugin.getLogger().getHandlers();
            for (Handler handler : handlers)
                if (handler instanceof UnloadHookLoggerHandler)
                    return bungeePlugin;

            bungeePlugin.getLogger().addHandler(new UnloadHookLoggerHandler(bungeePlugin));
            return bungeePlugin;
        }

        throw new IllegalArgumentException("Object " + plugin + " (" + plugin.getClass().getName() + ") is not a plugin.");
    }

    @Override
    public void close() {
        for (Plugin plugin : proxy.getPluginManager().getPlugins())
            for (Handler handler : plugin.getLogger().getHandlers())
                if (handler instanceof UnloadHookLoggerHandler)
                    plugin.getLogger().removeHandler(handler);
        super.close();
    }

    private final class UnloadHookLoggerHandler extends Handler {
        private final Plugin plugin;

        private UnloadHookLoggerHandler(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void close() {
            unregisterHandlers(this.plugin);
        }

        @Override public void publish(LogRecord record) {
        }

        @Override public void flush() {
        }
    }
}
