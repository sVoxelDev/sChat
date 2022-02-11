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

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.platform.sender.SenderMock;
import net.silthus.schat.ui.view.View;

@Getter
@Setter
@Accessors(fluent = true)
public class User {
    UUID id;
    String name;
    Server server;
    Channel channel;
    SenderMock sender;

    public User channel(Channel channel) {
        this.channel = channel;
        chatter().activeChannel(channel);
        return this;
    }

    public User server(Server server) {
        updateChatterStub(server);
        this.server = server;
        return this;
    }

    private void updateChatterStub(Server server) {
        if (this.server != server && this.server != null)
            removeFromServer(this.server);
        if (server != null)
            addToServer(server);
    }

    private void addToServer(Server server) {
        server.join(sender());
    }

    private void removeFromServer(Server server) {
        server.leave(sender());
    }

    public Chatter chatter() {
        return server().plugin().chatterRepository().get(id());
    }

    public void execute(String command) {
        server().plugin().commands().execute(sender(), command);
    }

    public void setPermission(String permission, boolean state) {
        sender().mockPermission(permission, state);
    }

    public View view() {
        return server().plugin().viewProvider().view(chatter());
    }

    public Message lastMessage() {
        return chatter().lastMessage().orElse(null);
    }

    @Override
    public String toString() {
        return name + "@" + server();
    }
}
