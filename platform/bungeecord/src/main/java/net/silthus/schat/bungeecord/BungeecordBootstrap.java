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

package net.silthus.schat.bungeecord;

import java.nio.file.Path;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.silthus.schat.bungeecord.adapter.BungeecordSchedulerAdapter;
import net.silthus.schat.platform.plugin.bootstrap.Bootstrap;
import net.silthus.schat.platform.plugin.bootstrap.LoaderBootstrap;
import net.silthus.schat.platform.plugin.bootstrap.Platform;
import net.silthus.schat.platform.plugin.logging.JavaPluginLogger;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;

@Getter
@Accessors(fluent = true)
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
    public Path dataDirectory() {
        return loader.getDataFolder().toPath();
    }

    @Override
    public String version() {
        return loader.getDescription().getVersion();
    }

    @Override
    public Platform.Type type() {
        return Platform.Type.BUNGEECORD;
    }

    @Override
    public String serverBrand() {
        return loader.getProxy().getName();
    }

    @Override
    public String serverVersion() {
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
