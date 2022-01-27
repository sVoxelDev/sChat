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

package net.silthus.schat.platform.plugin;

import cloud.commandframework.CommandManager;
import lombok.Getter;
import net.silthus.schat.platform.chatter.AbstractChatterFactory;
import net.silthus.schat.platform.chatter.ChatterFactoryStub;
import net.silthus.schat.platform.commands.Command;
import net.silthus.schat.platform.commands.Commands;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.listener.ChatListener;
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
        chatterFactory = new ChatterFactoryStub(viewProvider);
        return chatterFactory;
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
