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
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.policies.Policies;
import net.silthus.schat.policies.PoliciesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.JOIN_PERMISSION;
import static net.silthus.schat.channel.Channel.PROTECTED;
import static net.silthus.schat.channel.ChannelHelper.ConfiguredSetting.set;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.Chatter.chatter;
import static net.silthus.schat.identity.IdentityHelper.randomIdentity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PoliciesTests {
    private Chatter.PermissionHandler permissionHandler;
    private Policies policies;
    private Chatter chatter;

    @BeforeEach
    void setUp() {
        policies = new PoliciesImpl();
        permissionHandler = mock(Chatter.PermissionHandler.class);
        chatter = chatter(randomIdentity()).permissionHandler(permissionHandler).create();
    }

    private void assertCanJoin(Channel channel, boolean expected) {
        assertThat(policies.canJoinChannel(chatter, channel)).isEqualTo(expected);
    }

    private void mockHasPermission(String permission) {
        when(permissionHandler.hasPermission(permission)).thenReturn(true);
    }

    @Nested class given_protected_channel {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = channelWith(PROTECTED, true);
        }

        @Test
        void can_join_fails() {
            assertCanJoin(channel, false);
        }

        @Nested class given_user_has_permission {
            @BeforeEach
            void setUp() {
                mockHasPermission(channel.get(JOIN_PERMISSION));
            }

            @Test
            void can_join_succeeds() {
                assertCanJoin(channel, true);
            }

            @Nested class given_explicit_join_permission {
                @BeforeEach
                void setUp() {
                    channel = channelWith(set(PROTECTED, true), set(JOIN_PERMISSION, "my-permission"));
                    mockHasPermission("my-permission");
                }

                @Test
                void can_join_succeeds() {
                    assertCanJoin(channel, true);
                }
            }
        }
    }

    @Nested class given_unprotected_channel {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = channelWith(PROTECTED, false);
        }

        @Test
        void can_join_channel_succeeds() {
            assertCanJoin(channel, true);
        }
    }

    @Nested class given_channel_without_explicit_protection_setting {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = randomChannel();
        }

        @Test
        void can_join_succeeds() {
            assertCanJoin(channel, true);
        }
    }
}
