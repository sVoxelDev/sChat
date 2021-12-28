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

import java.io.File;
import lombok.Getter;
import net.silthus.schat.bukkit.listener.PlayerListener;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapters;
import net.silthus.schat.platform.plugin.AbstractPlugin;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class SChatBukkitPlugin extends AbstractPlugin {

    @Getter
    private final SChatBukkitBootstrap bootstrap;

    public SChatBukkitPlugin(SChatBukkitBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    protected @NotNull ConfigurationAdapter provideConfigurationAdapter() {
        return ConfigurationAdapters.YAML.create(new File(getBootstrap().getDataFolder(), "config.yml"));
    }

    @Override
    protected void registerListeners() {
        final PlayerListener playerListener = new PlayerListener(this);
        Bukkit.getPluginManager().registerEvents(playerListener, getBootstrap());
    }
}
