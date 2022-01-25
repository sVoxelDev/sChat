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

package net.silthus.schat.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.ProxyVersion;
import java.nio.file.Path;
import lombok.Getter;
import net.silthus.schat.platform.plugin.bootstrap.Bootstrap;
import net.silthus.schat.platform.plugin.bootstrap.Platform;
import net.silthus.schat.platform.plugin.logging.PluginLogger;
import net.silthus.schat.velocity.adapter.Slf4jPluginLogger;
import net.silthus.schat.velocity.adapter.VelocitySchedulerAdapter;
import org.slf4j.Logger;

@Plugin(id = "schat",
        name = "sChat",
        version = "1.0.0",
        url = "https://github.com/sVoxelDev/sChat",
        description = "Supercharge your Minecraft Chat Experience!",
        authors = {"Silthus"},
        dependencies = {@Dependency(id = "Protocolize")}
)

@Getter
public final class VelocityBootstrap implements Bootstrap {

    private final PluginLogger pluginLogger;
    private final VelocitySchedulerAdapter scheduler;
    private final VelocityPlugin plugin;

    @Inject
    private ProxyServer proxy;
    @Inject
    private PluginContainer pluginContainer;

    @Inject
    @DataDirectory
    private Path dataDirectory;

    @Inject
    public VelocityBootstrap(Logger logger) {
        this.pluginLogger = new Slf4jPluginLogger(logger);
        this.scheduler = new VelocitySchedulerAdapter(this);

        this.plugin = new VelocityPlugin(this);
    }

    @Override
    public Path getDataDirectory() {
        return this.dataDirectory.toAbsolutePath();
    }

    @Override
    public String getVersion() {
        return pluginContainer.getDescription().getVersion().orElse("UNKNOWN");
    }

    @Override
    public Platform.Type getType() {
        return Platform.Type.VELOCITY;
    }

    @Override
    public String getServerBrand() {
        final ProxyVersion version = proxy.getVersion();
        return version.getName() + " - " + version.getVendor() + " v" + version.getVersion();
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onEnable(ProxyInitializeEvent e) {
        this.plugin.load();
        this.plugin.enable();
    }

    @Subscribe(order = PostOrder.LAST)
    public void onDisable(ProxyShutdownEvent e) {
        this.plugin.disable();
    }
}
