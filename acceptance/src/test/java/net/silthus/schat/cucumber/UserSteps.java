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

import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.cucumber.models.User;
import net.silthus.schat.platform.sender.SenderMock;
import net.silthus.schat.ui.View;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.identity.Identity.identity;
import static net.silthus.schat.platform.locale.Messages.JOINED_CHANNEL;
import static net.silthus.schat.platform.locale.Messages.JOIN_CHANNEL_ERROR;

public class UserSteps {

    private final Context context;

    @Inject
    public UserSteps(Context context) {
        this.context = context;
    }

    public User me() {
        return user("I");
    }

    @ParameterType("I|[a-zA-Z0-9]+")
    public User user(String user) {
        return context.users().computeIfAbsent(user, this::createUser);
    }

    @ParameterType(value = "my view|(the view of (?<user>[a-zA-Z0-9]+))")
    public View view(String user) {
        if (user == null)
            return me().view();
        return user(user).view();
    }

    @DataTableType
    public User user(Map<String, String> entry) {
        return createUser(entry.get("name"))
            .server(context.server(entry.get("server")))
            .channel(context.channel(entry.get("channel")));
    }

    public User createUser(String user) {
        final SenderMock sender = SenderMock.sender(identity(user));
        context.addSenderToAllServers(sender);
        return new User()
            .sender(sender)
            .id(sender.uniqueId())
            .name(user)
            .server(context.primaryServer());
    }

    @Given("the following users")
    public void theFollowingUsers(List<User> users) {
        for (final User user : users) {
            context.users().put(user.name(), user);
        }
    }

    @Given("{user} have/has the {string} permission")
    public void setPermission(User user, String permission) {
        user.setPermission(permission, true);
    }

    @Given("I am {user}")
    public void iAmPlayer(User user) {
        context.users().put("I", user);
    }

    @When("{user} send(s) a private message to {user}")
    public void sendPrivateMessage(User source, User target) {
        source.execute("/tell " + target.name() + " Hi there!");
        context.lastMessageText(text("Hi there!"));
    }

    @When("{user} execute(s) {string}")
    public void executeCommand(User user, String command) {
        user.execute(command);
    }

    @Then("{user} received the joined {channel} channel message")
    public void receivedJoinedChannelMessage(User user, Channel channel) {
        user.sender().assertLastMessageIs(JOINED_CHANNEL.build(channel));
    }

    @Then("{user} received the cannot join {channel} channel message")
    public void receivedCannotJoinChannelMessage(User user, Channel channel) {
        user.sender().assertLastMessageIs(JOIN_CHANNEL_ERROR.build(channel));
    }

    @Then("{view} shows the message")
    public void theMessageIsShownInASeparateTab(View view) {
        view.render().contains(context.lastMessageText());
    }
}
