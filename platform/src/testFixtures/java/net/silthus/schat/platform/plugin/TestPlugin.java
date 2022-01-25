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
import net.silthus.schat.chatter.ChatterFactoryStub;
import net.silthus.schat.platform.commands.Command;
import net.silthus.schat.platform.commands.Commands;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.factories.AbstractChatterFactory;
import net.silthus.schat.platform.listener.ChatListener;
import net.silthus.schat.platform.plugin.bootstrap.Bootstrap;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.view.ViewProvider;

import static net.silthus.schat.platform.commands.CommandTestUtils.createCommandManager;
import static net.silthus.schat.platform.config.TestConfigurationAdapter.testConfigAdapter;
import static net.silthus.schat.platform.sender.SenderMock.senderMock;
import static org.mockito.Mockito.spy;

public class TestPlugin extends AbstractSChatPlugin {

    static Command dummyCommand = spy(Command.class);

    @Override
    public Sender getConsole() {
        return senderMock();
    }

    @Override
    protected ConfigurationAdapter provideConfigurationAdapter() {
        return testConfigAdapter();
    }

    @Override
    protected void setupSenderFactory() {

    }

    @Override
    protected AbstractChatterFactory provideChatterFactory(final ViewProvider viewProvider) {
        return new ChatterFactoryStub(viewProvider);
    }

    @Override
    protected ChatListener provideChatListener() {
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
}
