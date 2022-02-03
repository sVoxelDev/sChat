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

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
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
import net.silthus.schat.platform.messaging.CrossServerMessengerMock;
import net.silthus.schat.platform.messaging.StubMessengerGatewayProvider;
import net.silthus.schat.platform.plugin.AbstractSChatPlugin;
import net.silthus.schat.platform.plugin.TestPlugin;
import net.silthus.schat.platform.sender.SenderMock;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.Channel.GLOBAL;
import static net.silthus.schat.channel.Channel.PROTECTED;
import static net.silthus.schat.commands.SetActiveChannelCommand.setActiveChannel;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.message.MessageHelper.randomText;
import static org.assertj.core.api.Assertions.assertThat;

public class StepDefinitions {

    private final Map<String, TestPlugin> SERVERS = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();
    private Message lastMessage;
    private User user;

    public StepDefinitions() {
        createServer("server1");
    }

    @After
    public void disablePlugin() {
        SERVERS.values().forEach(AbstractSChatPlugin::disable);
    }

    @Before
    public void clearChannels() {
        SERVERS.values().stream()
            .map(AbstractSChatPlugin::getChannelRepository)
            .forEach(repository -> repository.all().forEach(repository::remove));
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
            .map(AbstractSChatPlugin::getChannelRepository);
    }

    private User getUser(String user) {
        return users.get(user);
    }

    private Chatter getChatter(String user) {
        return getChatter(getUser(user));
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

    private TestPlugin getServer(User user) {
        return SERVERS.get(user.server);
    }

    @DataTableType
    public User userEntry(Map<String, String> entry) {
        return new User()
            .name(entry.get("name"))
            .server(entry.get("server"))
            .channel(entry.get("channel"));
    }

    @Given("a second server {string}")
    public void aSecondServer(String server) {
        createServer(server);
    }

    private void createServer(String server) {
        final TestPlugin plugin = new TestPlugin();
        plugin.load();
        plugin.getGatewayProviderRegistry().register(new StubMessengerGatewayProvider("acceptance", new CrossServerMessengerMock(plugin, SERVERS.values())));
        plugin.enable();
        SERVERS.put(server, plugin);
    }

    @And("the following users")
    public void theFollowingUsers(List<User> users) {
        for (final User user : users) {
            createUser(user);
        }
    }

    private User createUser(User user) {
        final SenderMock senderMock = SenderMock.senderMock(Identity.identity(user.name));
        user.id = senderMock.getUniqueId();
        user.sender = senderMock;
        SERVERS.get(user.server).getChatterFactory().stubSenderAsChatter(senderMock);
        if (user.channel != null)
            setActiveChannel(getChatter(user), getChannel(user.server, user.channel)).execute();
        this.users.put(user.name, user);
        return user;
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

    @When("user {string} sends a message")
    public void userSendsAMessage(String user) {
        final Chatter chatter = getChatter(user);
        final Channel channel = chatter.getActiveChannel().orElseThrow();
        lastMessage = message(randomText()).source(chatter).to(channel).send();
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

    @Then("user {string} receives the message")
    public void userReceivesTheMessage(String user) {
        assertThat(getChatter(user).getLastMessage())
            .isPresent().get().isEqualTo(lastMessage);
    }

}
