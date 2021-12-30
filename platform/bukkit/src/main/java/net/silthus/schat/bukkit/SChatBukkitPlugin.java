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
import cloud.commandframework.CommandTree;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import java.util.function.Function;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.silthus.schat.bukkit.listener.PlayerListener;
import net.silthus.schat.platform.PlayerAdapter;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapters;
import net.silthus.schat.platform.plugin.AbstractPlugin;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        final Function<CommandTree<Sender>, CommandExecutionCoordinator<Sender>> executionCoordinatorFunction =
            AsynchronousCommandExecutionCoordinator.<Sender>newBuilder().build();

        try {
            return new PaperCommandManager<>(
                getBootstrap().getLoader(),
                executionCoordinatorFunction,
                commandSender -> getSenderFactory().wrap(commandSender),
                Sender::getHandle
            );
        } catch (Exception e) {
            getLogger().severe("Failed to initialize the command manager.");
            Bukkit.getPluginManager().disablePlugin(getBootstrap().getLoader());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void registerListeners() {
        final PlayerListener playerListener = new PlayerListener(this);
        Bukkit.getPluginManager().registerEvents(playerListener, getBootstrap().getLoader());
    }

    public PlayerAdapter<Player> getPlayerAdapter() {
        return getBootstrap().getPlayerAdapter(Player.class);
    }

    public User adapt(Player player) {
        return getPlayerAdapter().adapt(player);
    }
}
