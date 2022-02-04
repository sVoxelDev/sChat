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
import io.cucumber.java.en.But;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.platform.locale.Messages;
import net.silthus.schat.platform.plugin.AbstractSChatServerPlugin;
import net.silthus.schat.platform.plugin.TestServer;
import net.silthus.schat.platform.sender.SenderMock;
import net.silthus.schat.ui.model.ViewTab;
import net.silthus.schat.ui.view.View;
import net.silthus.schat.ui.views.TabbedChannelsView;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.Channel.GLOBAL;
import static net.silthus.schat.channel.Channel.PROTECTED;
import static net.silthus.schat.commands.SetActiveChannelCommand.setActiveChannel;
import static net.silthus.schat.message.MessageHelper.randomText;
import static org.assertj.core.api.Assertions.assertThat;

public class StepDefinitions {

    private final Context context = new Context();

    private final Map<String, User> users = new HashMap<>();
    private Message lastMessage;
    private User user;

    public StepDefinitions() {
    }

    private void addChannel(String key, Function<Channel.Builder, Channel.Builder> cfg) {
        getChannelRepositories().forEach(repository -> repository.add(createChannel(key, cfg)));
    }

    private Channel createChannel(String key, Function<Channel.Builder, Channel.Builder> cfg) {
        return cfg.apply(Channel.channel(key)).create();
    }

    private void setChannel(String channel, Consumer<Channel> config) {
        findChannel(channel).forEach(config);
    }

    private Channel getChannel(String channel) {
        return findChannel(channel).findFirst().orElseThrow();
    }

    @NotNull
    private Stream<Channel> findChannel(String channel) {
        return getChannelRepositories()
            .map(repository -> repository.find(channel))
            .flatMap(Optional::stream);
    }

    @NotNull
    private Stream<ChannelRepository> getChannelRepositories() {
        return SERVERS.values().stream()
            .map(AbstractSChatServerPlugin::getChannelRepository);
    }

    private User getUser(String user) {
        return users.get(user);
    }

    private Chatter getChatter() {
        return getChatter(user);
    }

    private Channel getChannel(String server, String channel) {
        return SERVERS.get(server).getChannelRepository().get(channel);
    }

    private Chatter getChatter(User user) {
        return getServer(user).getChatterProvider().get(user.id);
    }

    private TestServer getServer(User user) {
        return SERVERS.get(user.server);
    }

    @DataTableType
    public User userEntry(Map<String, String> entry) {
        return new User()
            .name(entry.get("name"))
            .server(entry.get("server"))
            .channel(entry.get("channel"));
    }

    private User createUser(User user) {
        final SenderMock senderMock = SenderMock.senderMock(Identity.identity(user.name));
        user.id = senderMock.uniqueId();
        user.sender = senderMock;
        SERVERS.get(user.server).getChatterFactory().stubSenderAsChatter(senderMock);
        if (user.channel != null)
            setActiveChannel(getChatter(user), getChannel(user.server, user.channel)).execute();
        this.users.put(user.name, user);
        return user;
    }

    private void sendPrivateMessage(Chatter source, Chatter destination) {
        lastMessage = source.message("Hey").to(destination).send();
    }

    @Given("a second server {server}")
    public void aSecondServer(TestServer server) {
    }

    @And("the following users")
    public void theFollowingUsers(List<User> users) {
        for (final User user : users) {
            createUser(user);
        }
    }

    @And("a global channel {string}")
    public void aGlobalChannel(String channel) {
        addChannel(channel, builder -> builder.set(GLOBAL, true));
    }

    @Given("a user")
    public void user() {
        this.user = createUser(new User().name("player1").server("server1").channel(null));
    }

    @Given("a public channel {string}")
    public void aPublicChannel(String channel) {
        addChannel(channel, builder -> builder.set(PROTECTED, false));
    }

    @Given("a protected channel {string}")
    public void aProtectedChannelProtected(String channel) {
        addChannel(channel, builder -> builder.set(PROTECTED, true));
    }

    @When("user runs {string}")
    public void userExecutesCommand(String command) {
        executeCommand(user, command);
    }

    private void executeCommand(User user, String command) {
        getServer(user).getCommands().execute(user.sender, command);
    }

    @When("{user} sends a message")
    public void userSendsAMessage(User user) {
        final Chatter chatter = getChatter(user);
        final Channel channel = chatter.activeChannel().orElseThrow();
        lastMessage = Message.message(randomText()).source(chatter).to(channel).send();
    }

    @When("{user} sends a private message to {user}")
    public void playerSendsAPrivateMessage(User source, User destination) {
        sendPrivateMessage(getChatter(source), getChatter(destination));
    }

    @Then("user receives join channel error message for channel {string}")
    public void userReceivesErrorMessage(String channel) {
        user.sender.assertLastMessageIs(Messages.JOIN_CHANNEL_ERROR.build(getChannel(channel)));
    }

    @Then("user is member of channel {string}")
    public void userIsMemberOfChannel(String channel) {
        assertThat(getChatter().isJoined(getChannel(channel))).isTrue();
    }

    @Then("user is not a member of channel {string}")
    public void userIsNotAMemberOfChannel(String channel) {
        assertThat(getChatter().isJoined(getChannel(channel))).isFalse();
    }

    @And("user has no permissions")
    public void noPermission() {
        user.sender.mockNoPermission();
    }

    @And("user has permission {string}")
    public void userHasPermission(String permission) {
        user.sender.mockPermission(permission, true);
    }

    @Then("user received joined channel {string} message")
    public void userReceivedJoinedChannel(String channel) {
        user.sender.assertLastMessageIs(Messages.JOINED_CHANNEL.build(getChannel(channel)));
    }

    @Then("{user} receives the message")
    public void userReceivesTheMessage(User user) {
        assertThat(getChatter(user).lastMessage())
            .isPresent().get().isEqualTo(lastMessage);
    }

    @But("{user} does not receive a message")
    public void playerDoesNotReceiveAMessage(User user) {
        assertThat(getChatter(user).lastMessage()).isEmpty();
    }

    @And("the {view} shows the {message} in a separate tab")
    public void theMessageIsShownInASeparateTab(View view, Message message) {
        assertThat(((TabbedChannelsView) view).getViewModel().tabs())
            .filteredOn(viewTab -> viewTab.source().equals(message.source()))
            .isNotEmpty()
            .extracting(ViewTab::messages).asList()
            .contains(message);
    }

    @ParameterType("view of ([a-zA-Z0-9]+)")
    public View view(String user) {
        final User u = getUser(user);
        return getServer(u).getViewProvider().getView(getChatter(u));
    }

    @ParameterType("message of ([a-zA-Z0-9]+)")
    public Message message(String user) {
        final User u = getUser(user);
        return getChatter(u).lastMessage().orElse(null);
    }
}
