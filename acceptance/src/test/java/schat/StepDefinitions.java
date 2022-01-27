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

package schat;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.platform.locale.Messages;
import net.silthus.schat.platform.plugin.TestPlugin;
import net.silthus.schat.platform.sender.SenderMock;

import static net.silthus.schat.channel.Channel.PROTECTED;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelHelper.ConfiguredSetting.set;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static org.assertj.core.api.Assertions.assertThat;

public class StepDefinitions {

    private static final TestPlugin plugin = new TestPlugin();
    private SenderMock user;
    private Channel channel;

    @BeforeAll(order = 1)
    public static void loadPlugin() {
        plugin.load();
    }

    @BeforeAll(order = 2)
    public static void enablePlugin() {
        plugin.enable();
    }

    @AfterAll
    public static void disablePlugin() {
        plugin.disable();
    }

    @Before
    public void clearChannels() {
        for (Channel c : plugin.getChannelRepository().all()) {
            plugin.getChannelRepository().remove(c);
        }
    }

    @Given("user")
    public void user() {
        user = SenderMock.senderMock();
        plugin.getChatterFactory().stubSenderAsChatter(user);
    }

    @Given("a public channel {string}")
    public void aPublicChannel(String channel) {
        this.channel = createChannel(channel);
        plugin.getChannelRepository().add(this.channel);
    }

    @Given("a protected channel {string}")
    public void aProtectedChannelProtected(String channel) {
        this.channel = channelWith(channel, set(PROTECTED, true));
        plugin.getChannelRepository().add(this.channel);
    }

    @When("user runs {string}")
    public void userExecutesCommand(String command) {
        plugin.getCommands().execute(user, command);
    }

    @Then("user receives join channel error message")
    public void userReceivesErrorMessage() {
        user.assertLastMessageIs(Messages.JOIN_CHANNEL_ERROR.build(this.channel));
    }

    @Then("user is member of channel {string}")
    public void userIsMemberOfChannel(String channel) {
        assertThat(chatter().isJoined(channel(channel))).isTrue();
    }

    private Chatter chatter() {
        return plugin.getChatterProvider().get(user.getUniqueId());
    }

    @ParameterType("channel")
    public Channel channel(String key) {
        return plugin.getChannelRepository().get(key);
    }

    @Then("user is not a member of channel {string}")
    public void userIsNotAMemberOfChannel(String channel) {
        assertThat(chatter().isJoined(channel(channel))).isFalse();
    }

    @And("user has no permissions")
    public void noPermission() {
        user.mockNoPermission();
    }

    @And("user has permission {string}")
    public void userHasPermission(String permission) {
        user.mockPermission(permission, true);
    }

    @Then("user received joined channel {string} message")
    public void userReceivedJoinedChannel(String channel) {
        user.assertLastMessageIs(Messages.JOINED_CHANNEL.build(channel(channel)));
    }
}
