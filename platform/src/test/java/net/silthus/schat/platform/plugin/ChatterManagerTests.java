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

package net.silthus.schat.platform.plugin;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.handler.types.PermissionHandler;
import net.silthus.schat.platform.SenderMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.JOIN_PERMISSION;
import static net.silthus.schat.channel.Channel.REQUIRES_JOIN_PERMISSION;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.channel.Permission.of;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static net.silthus.schat.identity.Identity.identity;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.platform.ChannelHelper.channelWith;
import static net.silthus.schat.platform.ChannelHelper.createChannelWith;
import static net.silthus.schat.platform.SenderMock.senderMock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatterManagerTests {

    private ChannelRepository channels;
    private ChatterManager chatters;
    private SenderMock sender;
    private PermissionHandler permission;
    private Chatter chatter;

    @BeforeEach
    void setUp() {
        channels = createInMemoryChannelRepository();
        chatters = new ChatterManager(createInMemoryChatterRepository());
        permission = mock(PermissionHandler.class);
        sender = spy(senderMock(identity("test"), permission));
        chatter = chatters.get(sender);
    }

    private void mockNoPermission() {
        when(permission.hasPermission(anyString())).thenReturn(false);
    }

    private void mockHasPermission(String permission) {
        when(this.permission.hasPermission(permission)).thenReturn(true);
    }

    private void assertJoinError(Chatter chatter, Channel channel) {
        assertThatExceptionOfType(Chatter.JoinChannel.AccessDenied.class)
            .isThrownBy(() -> chatter.join(channel));
    }

    private void assertJoinChannel(Chatter chatter, Channel channel) {
        chatter.join(channel);
        assertThat(chatter.getChannels()).contains(channel);
    }

    @Test
    void create_usesIdentityOfSender() {
        assertThat(chatter.getIdentity()).isSameAs(sender.getIdentity());
    }

    @Test
    void create_twice_reuses_existingChatter() {
        final Chatter chatter2 = chatters.get(sender);
        assertThat(chatter).isSameAs(chatter2);
    }

    @Test
    void sendMessage_usesSender() {
        final Chatter chatter = chatters.get(sender);
        chatter.sendMessage(message("Hi"));
        verify(sender).sendMessage(any());
    }

    @Test
    void given_channel_with_restricted_access_join_fails() {
        mockNoPermission();
        final Channel channel = channelWith(REQUIRES_JOIN_PERMISSION, true);

        assertJoinError(chatter, channel);
    }

    @Test
    void given_user_with_permission_can_join() {
        final Channel channel = channelWith(REQUIRES_JOIN_PERMISSION, true);
        mockHasPermission("schat.channel." + channel.getKey() + ".join");
        assertJoinChannel(chatter, channel);
    }

    @Test
    void given_user_with_different_permission_cannot_join() {
        mockHasPermission("schat.channel.test.join");
        final Channel channel = createChannelWith(builder -> builder
            .setting(REQUIRES_JOIN_PERMISSION, true)
            .setting(JOIN_PERMISSION, of("foobar")));

        assertJoinError(chatter, channel);
        mockHasPermission("foobar");
        assertJoinChannel(chatter, channel);
    }

    @Test
    void given_unprotected_channel_all_users_can_join() {
        mockNoPermission();
        assertJoinChannel(chatter, channelWith(REQUIRES_JOIN_PERMISSION, false));
    }

}
