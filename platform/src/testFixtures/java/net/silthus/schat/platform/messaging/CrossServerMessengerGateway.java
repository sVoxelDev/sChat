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
package net.silthus.schat.platform.messaging;

import java.util.Collection;
import net.silthus.schat.messenger.MessengerGateway;
import net.silthus.schat.platform.plugin.TestServer;

public class CrossServerMessengerGateway implements MessengerGateway {

    public static StubMessengerGatewayProvider provideCrossServerMessenger(Collection<TestServer> servers) {
        return new StubMessengerGatewayProvider(new CrossServerMessengerGateway(servers));
    }

    public static final String GATEWAY_TYPE = "acceptance";

    private final Collection<TestServer> servers;

    public CrossServerMessengerGateway(Collection<TestServer> servers) {
        this.servers = servers;
    }

    @Override
    public void sendOutgoingMessage(String encodedMessage) {
        for (TestServer server : servers) {
            server.messenger().consumeIncomingMessageAsString(encodedMessage);
        }
    }
}
