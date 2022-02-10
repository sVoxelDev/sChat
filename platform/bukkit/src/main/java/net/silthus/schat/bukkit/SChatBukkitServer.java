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
import lombok.experimental.Accessors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.silthus.schat.Messenger;
import net.silthus.schat.bukkit.adapter.BukkitChatterFactory;
import net.silthus.schat.bukkit.adapter.BukkitConnectionListener;
import net.silthus.schat.bukkit.adapter.BukkitMessengerGateway;
import net.silthus.schat.bukkit.adapter.BukkitSchedulerAdapter;
import net.silthus.schat.bukkit.adapter.BukkitSenderFactory;
import net.silthus.schat.bukkit.adapter.PlayerChatListener;
import net.silthus.schat.bukkit.protocollib.ChatPacketListener;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.platform.chatter.AbstractChatterFactory;
import net.silthus.schat.platform.chatter.ConnectionListener;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.messaging.GatewayProviderRegistry;
import net.silthus.schat.platform.plugin.AbstractSChatServerPlugin;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.ui.view.ViewProvider;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import static cloud.commandframework.execution.CommandExecutionCoordinator.simpleCoordinator;
import static net.silthus.schat.bukkit.adapter.BukkitMessengerGateway.createBukkitMessengerGateway;
import static net.silthus.schat.platform.config.adapter.ConfigurationAdapters.YAML;

@Getter
@Accessors(fluent = true)
public final class SChatBukkitServer extends AbstractSChatServerPlugin {

    private final BukkitBootstrap bootstrap;
    private BukkitSenderFactory senderFactory;
    private ChatPacketListener chatPacketListener;
    private PlayerChatListener chatListener;

    SChatBukkitServer(BukkitBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public Sender getConsole() {
        return senderFactory().wrap(bootstrap().loader().getServer().getConsoleSender());
    }

    @Override
    protected ConfigurationAdapter createConfigurationAdapter() {
        return YAML.create(resolveConfig("config.yml").toFile());
    }

    @Override
    protected void setupSenderFactory() {
        senderFactory = new BukkitSenderFactory(getAudiences(), new BukkitSchedulerAdapter(bootstrap.loader()));
    }

    @Override
    protected void registerMessengerGateway(GatewayProviderRegistry registry) {
        registry.register(BukkitMessengerGateway.GATEWAY_TYPE, consumer -> createBukkitMessengerGateway(
            bootstrap().loader(),
            Bukkit.getServer(),
            bootstrap().scheduler(),
            consumer,
            config()));
    }

    @Override
    protected AbstractChatterFactory createChatterFactory(final ViewProvider viewProvider) {
        return new BukkitChatterFactory(getAudiences(), viewProvider());
    }

    @Override
    protected ConnectionListener registerConnectionListener(ChatterRepository repository, ChatterFactory factory, Messenger messenger, EventBus eventBus) {
        return new BukkitConnectionListener(this);
    }

    @NotNull
    private BukkitAudiences getAudiences() {
        return BukkitAudiences.create(bootstrap().loader());
    }

    @Override
    @SneakyThrows
    protected CommandManager<Sender> provideCommandManager() {
        try {
            return new PaperCommandManager<>(
                bootstrap().loader(),
                simpleCoordinator(),
                commandSender -> senderFactory().wrap(commandSender),
                sender -> senderFactory().unwrap(sender)
            );
        } catch (Exception e) {
            logger().severe("Failed to initialize the command manager.");
            Bukkit.getPluginManager().disablePlugin(bootstrap().loader());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void registerListeners() {
        createChatListener();
        createChatPacketListener();
    }

    private void createChatListener() {
        chatListener = new PlayerChatListener(chatterRepository());
        Bukkit.getPluginManager().registerEvents(chatListener, bootstrap().loader());
    }

    private void createChatPacketListener() {
        chatPacketListener = new ChatPacketListener(bootstrap().loader(), chatterRepository(), viewProvider());
        chatPacketListener.enable();
    }
}
