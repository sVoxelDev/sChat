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

package net.silthus.schat.bungeecord;

import java.nio.file.Path;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.silthus.schat.bungeecord.adapter.BungeecordSchedulerAdapter;
import net.silthus.schat.platform.plugin.bootstrap.Bootstrap;
import net.silthus.schat.platform.plugin.bootstrap.LoaderBootstrap;
import net.silthus.schat.platform.plugin.bootstrap.Platform;
import net.silthus.schat.platform.plugin.logging.JavaPluginLogger;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;

@Getter
public final class BungeecordBootstrap implements Bootstrap, LoaderBootstrap {

    private final Plugin loader;
    private final ProxyServer proxy;
    private final SchedulerAdapter scheduler;
    private final BungeecordProxyPlugin plugin;

    private JavaPluginLogger pluginLogger;

    BungeecordBootstrap(Plugin loader) {
        this.loader = loader;
        this.proxy = loader.getProxy();
        this.scheduler = new BungeecordSchedulerAdapter(loader, loader.getProxy());

        this.plugin = new BungeecordProxyPlugin(this);
    }

    @Override
    public Path getDataDirectory() {
        return loader.getDataFolder().toPath();
    }

    @Override
    public String getVersion() {
        return loader.getDescription().getVersion();
    }

    @Override
    public Platform.Type getType() {
        return Platform.Type.BUNGEECORD;
    }

    @Override
    public String getServerBrand() {
        return loader.getProxy().getName();
    }

    @Override
    public String getServerVersion() {
        return loader.getProxy().getVersion();
    }

    @Override
    public void onLoad() {
        pluginLogger = new JavaPluginLogger(loader.getLogger());

        plugin.load();
    }

    @Override
    public void onEnable() {
        plugin.enable();
    }

    @Override
    public void onDisable() {
        plugin.disable();
    }
}
