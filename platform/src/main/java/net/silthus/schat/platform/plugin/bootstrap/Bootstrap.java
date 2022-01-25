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

package net.silthus.schat.platform.plugin.bootstrap;

import java.io.InputStream;
import java.nio.file.Path;
import net.silthus.schat.platform.plugin.logging.PluginLogger;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;

public interface Bootstrap {

    PluginLogger getPluginLogger();

    /**
     * Gets an adapter for the platforms scheduler.
     *
     * @return the scheduler
     */
    SchedulerAdapter getScheduler();

    /**
     * Gets the plugins main data storage directory.
     *
     * <p>Bukkit: /root/plugins/sChat</p>
     * <p>Bungee: /root/plugins/sChat</p>
     * <p>Sponge: /root/schat/</p>
     * <p>Fabric: /root/mods/sChat</p>
     *
     * @return the platforms data folder
     */
    Path getDataDirectory();

    /**
     * Gets the plugins configuration directory.
     *
     * @return the config directory
     */
    default Path getConfigDirectory() {
        return getDataDirectory();
    }

    /**
     * Gets a bundled resource file from the jar.
     *
     * @param path the path of the file
     * @return the file as an input stream
     */
    default InputStream getResourceStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    String getVersion();

    Platform.Type getType();

    String getServerBrand();
}
