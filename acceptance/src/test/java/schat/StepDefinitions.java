/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

    @Given("a user")
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
