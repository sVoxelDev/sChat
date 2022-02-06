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

package net.silthus.schat.policies;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
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
import static net.silthus.schat.policies.CanJoinChannelPolicy.canJoinChannel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CanJoinChannelPolicyTest {

    private final Chatter.PermissionHandler permissionHandler = mock(Chatter.PermissionHandler.class);
    private Chatter chatter;
    private Channel channel;

    @BeforeEach
    void setUp() {
        chatter = chatter(randomIdentity()).permissionHandler(permissionHandler).create();
    }

    private void assertCanJoin(boolean expected) {
        assertThat(canJoinChannel(chatter, channel).validate()).isEqualTo(expected);
    }

    private void mockHasPermission(String permission) {
        when(permissionHandler.hasPermission(permission)).thenReturn(true);
    }

    @Nested class given_protected_channel {

        @BeforeEach
        void setUp() {
            channel = channelWith(PROTECTED, true);
        }

        @Test
        void can_join_fails() {
            assertCanJoin(false);
        }

        @Nested class given_user_has_permission {
            @BeforeEach
            void setUp() {
                mockHasPermission("schat.channel." + channel.key() + ".join");
            }

            @Test
            void can_join_succeeds() {
                assertCanJoin(true);
            }

            @Nested class given_explicit_join_permission {
                @BeforeEach
                void setUp() {
                    channel = channelWith(set(PROTECTED, true), set(JOIN_PERMISSION, "my-permission"));
                    mockHasPermission("my-permission");
                }

                @Test
                void can_join_succeeds() {
                    assertCanJoin(true);
                }
            }
        }
    }

    @Nested class given_unprotected_channel {

        @BeforeEach
        void setUp() {
            channel = channelWith(PROTECTED, false);
        }

        @Test
        void can_join_channel_succeeds() {
            assertCanJoin(true);
        }
    }

    @Nested class given_channel_without_explicit_protection_setting {

        @BeforeEach
        void setUp() {
            channel = randomChannel();
        }

        @Test
        void can_join_succeeds() {
            assertCanJoin(true);
        }
    }
}
