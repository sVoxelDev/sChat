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

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.BukkitCommandManager;
import lombok.SneakyThrows;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.silthus.schat.bukkit.adapter.BukkitSenderFactory;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.platform.plugin.AbstractSChatPlugin;

import static cloud.commandframework.execution.CommandExecutionCoordinator.simpleCoordinator;

public final class SChatBukkitPlugin extends AbstractSChatPlugin {

    private final SChatBukkitPluginBootstrap bootstrap;
    private BukkitSenderFactory chatterFactory;

    SChatBukkitPlugin(SChatBukkitPluginBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    protected void setupChatterFactory() {
        chatterFactory = new BukkitSenderFactory(BukkitAudiences.create(bootstrap));
    }

    @Override
    @SneakyThrows
    protected CommandManager<Chatter> provideCommandManager() {
        return new BukkitCommandManager<>(
            bootstrap,
            simpleCoordinator(),
            sender -> chatterFactory.wrap(sender),
            chatter -> chatterFactory.unwrap(chatter)
        );
    }
}
