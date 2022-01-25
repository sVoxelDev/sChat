/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.bukkit;

import cloud.commandframework.CommandManager;
import cloud.commandframework.paper.PaperCommandManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.silthus.schat.bukkit.adapter.BukkitChatListener;
import net.silthus.schat.bukkit.adapter.BukkitChatterFactory;
import net.silthus.schat.bukkit.adapter.BukkitSchedulerAdapter;
import net.silthus.schat.bukkit.adapter.BukkitSenderFactory;
import net.silthus.schat.bukkit.protocollib.ChatPacketListener;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapters;
import net.silthus.schat.platform.chatter.AbstractChatterFactory;
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
    protected ConfigurationAdapter provideConfigurationAdapter() {
        return ConfigurationAdapters.YAML.create(resolveConfig("config.yml").toFile());
    }

    @Override
    protected void setupSenderFactory() {
        senderFactory = new BukkitSenderFactory(getAudiences(), new BukkitSchedulerAdapter(bootstrap.getLoader()));
    }

    @Override
    protected AbstractChatterFactory provideChatterFactory(final ViewProvider viewProvider) {
        return new BukkitChatterFactory(getAudiences(), getViewProvider());
    }

    @NotNull
    private BukkitAudiences getAudiences() {
        return BukkitAudiences.create(getBootstrap().getLoader());
    }

    @Override
    protected ChatListener provideChatListener() {
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
        chatPacketListener = new ChatPacketListener(getBootstrap().getLoader(), getChatterProvider(), getViewProvider(), getMessenger());
        chatPacketListener.enable();
    }
}
