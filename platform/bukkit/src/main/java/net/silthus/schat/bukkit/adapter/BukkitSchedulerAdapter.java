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

import java.util.concurrent.Executor;
import net.silthus.schat.platform.plugin.scheduler.AbstractJavaScheduler;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitSchedulerAdapter extends AbstractJavaScheduler implements SchedulerAdapter {

    private final Executor sync;

    public BukkitSchedulerAdapter(JavaPlugin loader) {
        this.sync = r -> loader.getServer().getScheduler().scheduleSyncDelayedTask(loader, r);
    }

    @Override
    public Executor sync() {
        return this.sync;
    }

}
