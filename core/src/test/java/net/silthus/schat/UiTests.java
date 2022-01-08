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

package net.silthus.schat;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.message.Message;
import net.silthus.schat.policies.ChannelPolicies;
import net.silthus.schat.ui.Ui;
import net.silthus.schat.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.ChannelHelper.channelWith;
import static net.silthus.schat.ChannelHelper.randomChannel;
import static net.silthus.schat.TestHelper.assertNPE;
import static net.silthus.schat.channel.Channel.PROTECTED;
import static net.silthus.schat.message.Message.emptyMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UiTests {

    private Ui ui;
    private User user;
    private ChannelPolicies policies;

    @BeforeEach
    void setUp() {
        policies = mock(ChannelPolicies.class);
        when(policies.canJoinChannel(any(), any())).thenReturn(true);
        ui = new Ui(policies);
        user = spy(UserHelper.randomUser());
    }

    private void assertJoinedChannel(Channel channel) {
        assertThat(channel.getTargets()).contains(user);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void joinChannel_givenNullUserOrChannel_throws() {
        assertNPE(() -> ui.joinChannel(null, null));
        assertNPE(() -> ui.joinChannel(user, null));
    }

    @Test
    void joinChannel_givenUnprotectedChannel_addsUserToChannel() {
        final Channel channel = channelWith(PROTECTED, false);
        ui.joinChannel(user, channel);
        assertJoinedChannel(channel);
    }

    @Test
    void joinChannel_checksPolicies() {
        ui.joinChannel(user, randomChannel());
        verify(policies).canJoinChannel(any(), any());
    }

    @Test
    void joinChannel_givenCanJoinChannelFails_throws() {
        when(policies.canJoinChannel(any(), any())).thenReturn(false);
        assertThatExceptionOfType(Ui.JoinChannelError.class).isThrownBy(() -> ui.joinChannel(user, randomChannel()));
    }

    @Test
    void joinChannel_addsChannelToUser() {
        final Channel channel = randomChannel();
        ui.joinChannel(user, channel);
        assertThat(user.getChannels()).contains(channel);
    }

    @Test
    void givenJoinedChannel_sendsChannelMessage_sendsToUser() {
        final Channel channel = randomChannel();
        ui.joinChannel(user, channel);
        final Message message = emptyMessage();
        channel.sendMessage(message);
        verify(user).sendMessage(message);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void setActiveChannel_givenNullUserOrChannel_throws() {
        assertNPE(() -> ui.setActiveChannel(null, null));
        assertNPE(() -> ui.setActiveChannel(user, null));
    }

    @Test
    void setActiveChannel_joinsChannel() {
        final Channel channel = randomChannel();
        ui.setActiveChannel(user, channel);
        assertJoinedChannel(channel);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void chat_givenNullUserOrText_throws() {
        assertNPE(() -> ui.chat(null, null));
        assertNPE(() -> ui.chat(user, null));
    }

    @Test
    void chat_givenNoActiveChannel_throws() {
        assertThatExceptionOfType(Ui.NoActiveChannel.class)
            .isThrownBy(() -> ui.chat(user, text("Hi there!")));
    }

    @Test
    void chat_givenActiveChannel_sendsMessageToChannel() {
        final Channel channel = mock(Channel.class);
        ui.setActiveChannel(user, channel);
        final Message message = ui.chat(user, text("Hi"));
        verify(channel).sendMessage(message);
    }

    @Test
    void chat_setsSourceToUser() {
        final Channel channel = mock(Channel.class);
        ui.setActiveChannel(user, channel);
        Message message = ui.chat(user, text("Hi"));
        assertThat(message.getSource()).isEqualTo(user);
    }
}
