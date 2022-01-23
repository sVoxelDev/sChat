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

package net.silthus.schat.platform.plugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import lombok.SneakyThrows;
import net.silthus.schat.platform.plugin.bootstrap.Bootstrap;
import net.silthus.schat.platform.plugin.bootstrap.Platform;
import net.silthus.schat.platform.plugin.logging.JavaPluginLogger;
import net.silthus.schat.platform.plugin.logging.PluginLogger;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;

import static org.mockito.Mockito.mock;

public class BootstrapStub implements Bootstrap {
    @Override
    public PluginLogger getPluginLogger() {
        return new JavaPluginLogger(Logger.getLogger("Test"));
    }

    @Override
    public SchedulerAdapter getScheduler() {
        return mock(SchedulerAdapter.class);
    }

    @SneakyThrows
    @Override
    public Path getDataDirectory() {
        return Files.createTempDirectory("schat");
    }

    @Override
    public String getVersion() {
        return "0";
    }

    @Override
    public Platform.Type getType() {
        return Platform.Type.BUKKIT;
    }

    @Override
    public String getServerBrand() {
        return "test";
    }
}
