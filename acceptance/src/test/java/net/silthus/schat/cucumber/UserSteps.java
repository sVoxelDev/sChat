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
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.cucumber.models.User;
import net.silthus.schat.message.Message;
import net.silthus.schat.platform.sender.SenderMock;
import net.silthus.schat.ui.model.ViewTab;
import net.silthus.schat.ui.view.View;
import net.silthus.schat.ui.views.TabbedChannelsView;

import static net.silthus.schat.identity.Identity.identity;
import static net.silthus.schat.platform.locale.Messages.JOINED_CHANNEL;
import static net.silthus.schat.platform.locale.Messages.JOIN_CHANNEL_ERROR;
import static org.assertj.core.api.Assertions.assertThat;

@Singleton
public class UserSteps {

    private final Context context;

    @Inject
    public UserSteps(Context context) {
        this.context = context;
    }

    @ParameterType("I|[a-zA-Z0-9]+")
    public User user(String user) {
        return context.users().computeIfAbsent(user, this::createUser);
    }

    @ParameterType("view of ([a-zA-Z0-9]+)")
    public View view(String user) {
        return user(user).view();
    }

    @ParameterType("message of ([a-zA-Z0-9]+)")
    public Message message(String user) {
        return user(user).lastMessage();
    }

    @DataTableType
    public User user(Map<String, String> entry) {
        return createUser(entry.get("name"))
            .server(context.server(entry.get("server")))
            .channel(context.channel(entry.get("channel")));
    }

    @Given("the following users")
    public void theFollowingUsers(List<User> users) {
        // implicitly created by the data table lookup
    }

    public User createUser(String user) {
        final SenderMock sender = SenderMock.senderMock(identity(user));
        return new User()
            .sender(sender)
            .id(sender.uniqueId())
            .name(user)
            .server(context.primaryServer());
    }

    @When("{user} execute(s) {string}")
    public void executeCommand(User user, String command) {
        user.execute(command);
    }

    @And("{user} received the joined {channel} channel message")
    public void receivedJoinedChannelMessage(User user, Channel channel) {
        user.sender().assertLastMessageIs(JOINED_CHANNEL.build(channel));
    }

    @And("{user} received the cannot join {channel} channel message")
    public void iReceivedTheCannotJoinProtectedChannelMessage(User user, Channel channel) {
        user.sender().assertLastMessageIs(JOIN_CHANNEL_ERROR.build(channel));
    }

    @And("{user} have/has the {string} permission")
    public void setPermission(User user, String permission) {
        user.setPermission(permission, true);
    }

    @And("the {view} shows the {message} in a separate tab")
    public void theMessageIsShownInASeparateTab(View view, Message message) {
        assertThat(((TabbedChannelsView) view).viewModel().tabs())
            .filteredOn(viewTab -> viewTab.source().equals(message.source()))
            .isNotEmpty()
            .extracting(ViewTab::messages).asList()
            .contains(message);
    }
}
