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
package net.silthus.schat.cucumber.models;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.platform.messaging.StubMessengerGatewayProvider;
import net.silthus.schat.platform.plugin.TestServer;
import net.silthus.schat.platform.sender.SenderMock;

import static net.silthus.schat.platform.messaging.CrossServerMessengerGateway.GATEWAY_TYPE;

@Getter
@Setter
@Accessors(fluent = true)
public class Server {

    private final TestServer plugin = new TestServer();

    public Server() {
    }

    public void load() {
        plugin().load();
    }

    public void enable() {
        plugin().enable();
    }

    public void disable() {
        plugin().disable();
    }

    public void injectMessenger(StubMessengerGatewayProvider messenger) {
        plugin().gatewayProviderRegistry().register(GATEWAY_TYPE, messenger);
    }

    public void addChannel(Channel channel) {
        plugin().channelRepository().add(channel);
    }

    public void join(SenderMock sender) {
        plugin().joinServer(sender);
    }

    public void leave(SenderMock sender) {
        plugin().leaveServer(sender);
    }

    @Override
    public String toString() {
        return plugin().toString();
    }
}
