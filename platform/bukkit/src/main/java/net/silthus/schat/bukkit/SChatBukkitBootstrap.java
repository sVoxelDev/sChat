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

package net.silthus.schat.bukkit;

import java.nio.file.Path;
import lombok.Getter;
import net.silthus.schat.bukkit.adapter.BukkitSchedulerAdapter;
import net.silthus.schat.platform.plugin.bootstrap.Bootstrap;
import net.silthus.schat.platform.plugin.bootstrap.LoaderBootstrap;
import net.silthus.schat.platform.plugin.logging.JavaPluginLogger;
import net.silthus.schat.platform.plugin.logging.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class SChatBukkitBootstrap implements Bootstrap, LoaderBootstrap {

    private final JavaPlugin loader;
    private final SChatBukkitPlugin plugin;

    private final PluginLogger pluginLogger;
    private final BukkitSchedulerAdapter scheduler;

    public SChatBukkitBootstrap(JavaPlugin loader) {
        this.loader = loader;

        this.pluginLogger = new JavaPluginLogger(loader.getLogger());
        this.scheduler = new BukkitSchedulerAdapter(loader);
        this.plugin = new SChatBukkitPlugin(this);
    }

    @Override
    public void onLoad() {
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

    @Override
    public Path getDataDirectory() {
        return getLoader().getDataFolder().toPath().toAbsolutePath();
    }
}
