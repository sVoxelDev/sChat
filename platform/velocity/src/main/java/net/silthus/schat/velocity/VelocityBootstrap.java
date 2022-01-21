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
