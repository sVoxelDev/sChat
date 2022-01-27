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
                mockHasPermission("schat.channel." + channel.getKey() + ".join");
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
