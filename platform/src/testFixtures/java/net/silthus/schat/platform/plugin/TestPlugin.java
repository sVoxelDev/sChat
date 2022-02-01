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

import cloud.commandframework.CommandManager;
import lombok.Getter;
import net.silthus.schat.event.AbstractEventBus;
import net.silthus.schat.event.EventBus;
import net.silthus.schat.platform.chatter.AbstractChatterFactory;
import net.silthus.schat.platform.chatter.ChatterFactoryStub;
import net.silthus.schat.platform.commands.Command;
import net.silthus.schat.platform.commands.Commands;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.listener.ChatListener;
import net.silthus.schat.platform.messenger.CrossServerMessengerMock;
import net.silthus.schat.platform.plugin.bootstrap.Bootstrap;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.ui.view.ViewProvider;

import static net.silthus.schat.platform.commands.CommandTestUtils.createCommandManager;
import static net.silthus.schat.platform.config.TestConfigurationAdapter.testConfigAdapter;
import static net.silthus.schat.platform.sender.SenderMock.senderMock;
import static org.mockito.Mockito.spy;

@Getter
public class TestPlugin extends AbstractSChatPlugin {

    static Command dummyCommand = spy(Command.class);
    private ChatterFactoryStub chatterFactory;
    private CrossServerMessengerMock messenger;

    @Override
    public Sender getConsole() {
        return senderMock();
    }

    @Override
    protected ConfigurationAdapter createConfigurationAdapter() {
        return testConfigAdapter();
    }

    @Override
    protected EventBus createEventBus() {
        return new TestEventBus();
    }

    @Override
    protected void setupSenderFactory() {

    }

    @Override
    protected AbstractChatterFactory createChatterFactory(final ViewProvider viewProvider) {
        chatterFactory = new ChatterFactoryStub(viewProvider);
        return chatterFactory;
    }

    @Override
    protected ChatListener createChatListener() {
        return new ChatListener();
    }

    @Override
    protected CommandManager<Sender> provideCommandManager() {
        return createCommandManager();
    }

    @Override
    protected void registerCustomCommands(Commands commands) {
        commands.register(dummyCommand);
    }

    @Override
    public Bootstrap getBootstrap() {
        return new BootstrapStub();
    }

    public void setMessenger(CrossServerMessengerMock messenger) {
        this.messenger = messenger;
    }

    private final class TestEventBus extends AbstractEventBus<TestPlugin> {

        @Override
        protected TestPlugin checkPlugin(Object plugin) throws IllegalArgumentException {
            return TestPlugin.this;
        }
    }
}
