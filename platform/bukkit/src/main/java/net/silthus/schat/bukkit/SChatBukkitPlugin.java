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
import lombok.SneakyThrows;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.silthus.schat.bukkit.adapter.BukkitChatListener;
import net.silthus.schat.bukkit.adapter.BukkitChatterFactory;
import net.silthus.schat.bukkit.adapter.BukkitEventBus;
import net.silthus.schat.bukkit.adapter.BukkitSchedulerAdapter;
import net.silthus.schat.bukkit.adapter.BukkitSenderFactory;
import net.silthus.schat.bukkit.protocollib.ChatPacketListener;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.platform.chatter.AbstractChatterFactory;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapters;
import net.silthus.schat.platform.listener.ChatListener;
import net.silthus.schat.platform.plugin.AbstractSChatPlugin;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.ui.view.ViewProvider;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import static cloud.commandframework.execution.CommandExecutionCoordinator.simpleCoordinator;

@Getter
public final class SChatBukkitPlugin extends AbstractSChatPlugin {

    private final BukkitBootstrap bootstrap;
    private BukkitSenderFactory senderFactory;
    private ChatPacketListener chatPacketListener;

    SChatBukkitPlugin(BukkitBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public Sender getConsole() {
        return getSenderFactory().wrap(getBootstrap().getLoader().getServer().getConsoleSender());
    }

    @Override
    protected ConfigurationAdapter createConfigurationAdapter() {
        return ConfigurationAdapters.YAML.create(resolveConfig("config.yml").toFile());
    }

    @Override
    protected EventBus createEventBus() {
        final BukkitEventBus bus = new BukkitEventBus();
        Bukkit.getPluginManager().registerEvents(bus, getBootstrap().getLoader());
        return bus;
    }

    @Override
    protected void setupSenderFactory() {
        senderFactory = new BukkitSenderFactory(getAudiences(), new BukkitSchedulerAdapter(bootstrap.getLoader()));
    }

    @Override
    protected AbstractChatterFactory createChatterFactory(final ViewProvider viewProvider) {
        return new BukkitChatterFactory(getAudiences(), getViewProvider());
    }

    @NotNull
    private BukkitAudiences getAudiences() {
        return BukkitAudiences.create(getBootstrap().getLoader());
    }

    @Override
    protected ChatListener createChatListener() {
        final BukkitChatListener listener = new BukkitChatListener();
        Bukkit.getPluginManager().registerEvents(listener, getBootstrap().getLoader());
        return listener;
    }

    @Override
    @SneakyThrows
    protected CommandManager<Sender> provideCommandManager() {
        try {
            return new PaperCommandManager<>(
                getBootstrap().getLoader(),
                simpleCoordinator(),
                commandSender -> getSenderFactory().wrap(commandSender),
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
        chatPacketListener = new ChatPacketListener(getBootstrap().getLoader(), getChatterProvider(), getViewProvider());
        chatPacketListener.enable();
    }
}
