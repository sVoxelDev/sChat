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

package net.silthus.schat.cucumber;

import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.cucumber.models.Server;
import net.silthus.schat.cucumber.models.User;
import net.silthus.schat.message.Message;
import net.silthus.schat.platform.plugin.TestServer;
import net.silthus.schat.platform.sender.SenderMock;

import static net.silthus.schat.platform.messaging.CrossServerMessengerGateway.provideCrossServerMessenger;

@Getter
@Setter
@Accessors(fluent = true)
@ScenarioScoped
public class Context {
    public static final String PRIMARY_SERVER = "server1";

    private final UserSteps userSteps;
    private final ServerSteps serverSteps;
    private final ChannelSteps channelSteps;

    private final Map<String, Server> servers = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Channel> channels = new HashMap<>();

    private Message lastMessage;
    private Component lastMessageText;

    public Context() {
        this.userSteps = new UserSteps(this);
        this.serverSteps = new ServerSteps(this);
        this.channelSteps = new ChannelSteps(this);
    }

    @Before(order = 10)
    public void setup() {
        servers.put(PRIMARY_SERVER, serverSteps.createServer());
        servers.put("server2", serverSteps.createServer());
    }

    @Before(order = 20)
    public void loadServers() {
        servers.values().forEach(Server::load);
    }

    @Before(order = 30)
    public void injectCrossServerMessenger() {
        final List<TestServer> servers = this.servers.values().stream().map(Server::plugin).toList();
        this.servers.values().forEach(server -> server.injectMessenger(provideCrossServerMessenger(servers)));
    }

    @Before(order = 40)
    public void enableServers() {
        servers.values().forEach(Server::enable);
    }

    @After
    public void tearDown() {
        servers.values().forEach(Server::disable);
    }

    public Component lastMessageText() {
        return lastMessageText == null && lastMessage() != null ? lastMessage().text() : lastMessageText;
    }

    public User user(String name) {
        return users().getOrDefault(name, userSteps.user(name));
    }

    public Server primaryServer() {
        return servers.get(PRIMARY_SERVER);
    }

    public Server server(String server) {
        return serverSteps().server(server);
    }

    public Channel channel(String channel) {
        if (channel == null)
            return null;
        return channels.get(channel);
    }

    public Channel addChannelToAllServers(Channel channel) {
        servers().values().forEach(server -> server.addChannel(channel));
        return channel;
    }

    public void addSenderToAllServers(SenderMock sender) {
        servers().values().forEach(server -> server.plugin().chatterFactory().stubSenderAsChatter(sender));
    }
}
