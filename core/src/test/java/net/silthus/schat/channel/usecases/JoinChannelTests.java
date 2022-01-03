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

package net.silthus.schat.channel.usecases;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.Channels;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.permission.Permission;
import net.silthus.schat.permission.PermissionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.ChannelHelper.channelWith;
import static net.silthus.schat.ChannelHelper.createChannelWith;
import static net.silthus.schat.IdentityHelper.randomIdentity;
import static net.silthus.schat.channel.Channel.JOIN_PERMISSION;
import static net.silthus.schat.channel.Channel.REQUIRES_JOIN_PERMISSION;
import static net.silthus.schat.channel.Channels.channels;
import static net.silthus.schat.channel.repository.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.channel.usecases.JoinChannel.Args.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JoinChannelTests {

    private Channels channels;
    private PermissionHandler permission;
    private Chatter chatter;

    @BeforeEach
    void setUp() {
        permission = mock(PermissionHandler.class);
        channels = channels().repository(createInMemoryChannelRepository()).create();
        chatter = Chatter.chatter(randomIdentity()).permissionHandler(permission).create();
    }

    private void mockNoPermission() {
        when(permission.hasPermission(anyString())).thenReturn(false);
    }

    private void mockHasPermission(String permission) {
        when(this.permission.hasPermission(permission)).thenReturn(true);
    }

    private void assertJoinError(Chatter chatter, Channel channel) {
        assertThatExceptionOfType(JoinChannel.Error.class)
            .isThrownBy(() -> channels.joinChannel(of(chatter, channel)));
    }

    private void assertJoinChannel(Chatter chatter, Channel channel) {
        chatter.join(channel);
        assertThat(chatter.getChannels()).contains(channel);
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
            .setting(JOIN_PERMISSION, Permission.of("foobar")));

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
