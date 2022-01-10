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

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.silthus.schat.bukkit.adapter.BukkitUserFactory;
import net.silthus.schat.platform.SChatPlugin;
import net.silthus.schat.platform.UserFactory;

public final class SChatBukkitPlugin extends SChatPlugin {
    private final SChatBukkitPluginBootstrap bootstrap;

    SChatBukkitPlugin(SChatBukkitPluginBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    protected UserFactory<?> provideUserFactory() {
        return new BukkitUserFactory(BukkitAudiences.create(bootstrap));
    }
}
