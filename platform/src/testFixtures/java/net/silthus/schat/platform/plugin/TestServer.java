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
import lombok.experimental.Accessors;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.messenger.Messenger;
import net.silthus.schat.platform.chatter.AbstractChatterFactory;
import net.silthus.schat.platform.chatter.ChatterFactoryStub;
import net.silthus.schat.platform.chatter.ConnectionListener;
import net.silthus.schat.platform.commands.Command;
import net.silthus.schat.platform.commands.Commands;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.messaging.GatewayProviderRegistry;
import net.silthus.schat.platform.messaging.MockMessagingGatewayProvider;
import net.silthus.schat.platform.plugin.bootstrap.Bootstrap;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.platform.sender.SenderMock;
import net.silthus.schat.ui.view.ViewProvider;

import static net.silthus.schat.platform.commands.CommandTestUtils.createCommandManager;
import static net.silthus.schat.platform.config.TestConfigurationAdapter.testConfigAdapter;
import static org.mockito.Mockito.spy;

@Getter
@Accessors(fluent = true)
public class TestServer extends AbstractSChatServerPlugin {

    private static int serverCount = 0;

    private final String name;
    static Command dummyCommand = spy(Command.class);
    private ChatterFactoryStub chatterFactory;

    public TestServer() {
        name = "Server" + ++serverCount;
    }

    @Override
    public Sender getConsole() {
        return SenderMock.randomSender();
    }

    @Override
    protected ConfigurationAdapter createConfigurationAdapter() {
        return testConfigAdapter();
    }

    @Override
    protected void setupSenderFactory() {

    }

    @Override
    protected void registerMessengerGateway(GatewayProviderRegistry registry) {
        registry.register("mock", new MockMessagingGatewayProvider());
    }

    @Override
    protected AbstractChatterFactory createChatterFactory(final ViewProvider viewProvider) {
        chatterFactory = new ChatterFactoryStub(viewProvider);
        return chatterFactory;
    }

    @Override
    protected ConnectionListener registerConnectionListener(ChatterRepository repository, ChatterFactory factory, Messenger messenger, EventBus eventBus) {
        return new TestConnectionListener(repository, factory, messenger, eventBus);
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
    public Bootstrap bootstrap() {
        return new BootstrapStub();
    }

    public void joinServer(Sender sender) {
        ((TestConnectionListener) connectionListener()).joinServer(sender);
    }

    public void leaveServer(SenderMock sender) {
        chatterRepository().remove(sender.uniqueId());
    }

    @Override
    public String toString() {
        return name();
    }

    private static final class TestConnectionListener extends ConnectionListener {

        private TestConnectionListener(ChatterRepository chatterRepository, ChatterFactory chatterFactory, Messenger messenger, EventBus eventBus) {
            super(chatterRepository, chatterFactory, messenger, eventBus);
        }

        public void joinServer(Sender sender) {
            onJoin(sender);
        }
    }
}
