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
import net.silthus.schat.policies.ChannelPolicies;
import net.silthus.schat.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.ChannelHelper.channelWith;
import static net.silthus.schat.channel.Channel.JOIN_PERMISSION;
import static net.silthus.schat.channel.Channel.PROTECTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PoliciesTests {

    private ChannelPolicies policies;
    private User user;

    @BeforeEach
    void setUp() {
        policies = new ChannelPolicies();
        user = mock(User.class);
    }

    @Test
    void givenUnprotectedChannel_canJoinChannel_succeeds() {
        final Channel channel = channelWith(PROTECTED, false);
        assertThat(policies.canJoinChannel(user, channel)).isTrue();
    }

    @Test
    void givenProtectedChannel_canJoinChannel_fails() {
        final Channel channel = channelWith(PROTECTED, true);
        assertThat(policies.canJoinChannel(user, channel)).isFalse();
    }

    @Test
    void givenProtectedChannel_and_UserWithPermission_canJoinChannel_succeeds() {
        final Channel channel = channelWith(PROTECTED, true);
        when(user.hasPermission(channel.get(JOIN_PERMISSION))).thenReturn(true);
        assertThat(policies.canJoinChannel(user, channel)).isTrue();
    }
}
