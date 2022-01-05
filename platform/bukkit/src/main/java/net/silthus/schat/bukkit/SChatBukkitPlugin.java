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
import cloud.commandframework.paper.PaperCommandManager;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.silthus.schat.bukkit.adapter.BukkitSenderFactory;
import net.silthus.schat.bukkit.listener.PlayerListener;
import net.silthus.schat.bukkit.protocollib.ChatPacketListener;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapters;
import net.silthus.schat.platform.plugin.AbstractPlugin;
import net.silthus.schat.sender.Sender;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import static cloud.commandframework.execution.CommandExecutionCoordinator.simpleCoordinator;

@Getter
public final class SChatBukkitPlugin extends AbstractPlugin {

    private final SChatBukkitBootstrap bootstrap;
    private BukkitSenderFactory senderFactory;

    public SChatBukkitPlugin(SChatBukkitBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new BukkitSenderFactory(BukkitAudiences.create(getBootstrap().getLoader()), getBootstrap().getScheduler());
    }

    @Override
    protected @NotNull ConfigurationAdapter provideConfigurationAdapter() {
        return ConfigurationAdapters.YAML.create(resolveConfig("config.yml").toFile());
    }

    @Override
    protected CommandManager<Sender> provideCommandManager() {
        try {
            return new PaperCommandManager<>(
                getBootstrap().getLoader(),
                simpleCoordinator(),
                commandSender -> getSenderFactory().adapt(commandSender),
                sender -> getSenderFactory().unwrap(sender)
            );
        } catch (Exception e) {
            getLogger().severe("Failed to initialize the command manager.");
            Bukkit.getPluginManager().disablePlugin(getBootstrap().getLoader());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void registerListeners() {
        final PlayerListener playerListener = new PlayerListener(getSenderFactory(), getConnectionListener(), getChatters());
        Bukkit.getPluginManager().registerEvents(playerListener, getBootstrap().getLoader());
        final ChatPacketListener packetListener = new ChatPacketListener(getBootstrap().getLoader(), getSenderFactory(), getChatters());
        packetListener.enable();
    }
}
