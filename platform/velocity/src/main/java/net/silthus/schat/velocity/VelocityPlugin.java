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

package net.silthus.schat.velocity;

import cloud.commandframework.CommandManager;
import cloud.commandframework.velocity.VelocityCommandManager;
import dev.simplix.protocolize.api.Protocolize;
import java.io.File;
import lombok.Getter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapters;
import net.silthus.schat.platform.listener.ChatListener;
import net.silthus.schat.platform.plugin.AbstractSChatPlugin;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.velocity.adapter.VelocityChatListener;
import net.silthus.schat.velocity.adapter.VelocityChatterFactory;
import net.silthus.schat.velocity.adapter.VelocitySenderFactory;
import net.silthus.schat.velocity.protocolize.ChatPacketListener;
import net.silthus.schat.view.ViewProvider;

import static cloud.commandframework.execution.CommandExecutionCoordinator.simpleCoordinator;

@Getter
public final class VelocityPlugin extends AbstractSChatPlugin {

    private final VelocityBootstrap bootstrap;
    private VelocitySenderFactory senderFactory;
    private ChatPacketListener chatPacketListener;

    public VelocityPlugin(VelocityBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public Sender getConsole() {
        return getSenderFactory().wrap(bootstrap.getProxy().getConsoleCommandSource());
    }

    @Override
    protected ConfigurationAdapter provideConfigurationAdapter() {
        return ConfigurationAdapters.YAML.create(new File(bootstrap.getConfigDirectory().toFile(), "config.yml"));
    }

    @Override
    protected void setupSenderFactory() {
        senderFactory = new VelocitySenderFactory(bootstrap.getProxy());
    }

    @Override
    protected ChatterFactory provideChatterFactory(final ViewProvider viewProvider) {
        return new VelocityChatterFactory(getBootstrap().getProxy(), getViewProvider());
    }

    @Override
    protected ChatListener provideChatListener() {
        final VelocityChatListener listener = new VelocityChatListener();
        bootstrap.getProxy().getEventManager().register(bootstrap, listener);
        return listener;
    }

    @Override
    protected CommandManager<Sender> provideCommandManager() {
        return new VelocityCommandManager<>(
            bootstrap.getPluginContainer(),
            bootstrap.getProxy(),
            simpleCoordinator(),
            commandSource -> getSenderFactory().wrap(commandSource),
            sender -> getSenderFactory().unwrap(sender)
        );
    }

    @Override
    protected void registerListeners() {
        chatPacketListener = new ChatPacketListener(getChatterProvider(), getViewProvider(), getMessenger());
        Protocolize.listenerProvider().registerListener(chatPacketListener);
    }
}
