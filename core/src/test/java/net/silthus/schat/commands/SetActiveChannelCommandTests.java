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
package net.silthus.schat.commands;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.command.Result;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.policies.JoinChannelPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.commands.JoinChannelCommand.joinChannelBuilder;
import static net.silthus.schat.policies.JoinChannelPolicy.ALLOW;
import static net.silthus.schat.policies.JoinChannelPolicy.DENY;
import static org.assertj.core.api.Assertions.assertThat;

class SetActiveChannelCommandTests {

    private ChatterMock chatter;
    private Channel channel;

    @BeforeEach
    void setUp() {
        chatter = ChatterMock.randomChatter();
        channel = channelWith(builder -> builder.policy(JoinChannelPolicy.class, ALLOW));
        JoinChannelCommand.prototype(builder -> builder.eventBus(EventBus.empty()));
    }

    private Result setActiveChannel() {
        return SetActiveChannelCommand.setActiveChannel(chatter, channel);
    }

    @Nested class given_valid_channel {

        @Test
        void then_chatter_has_active_channel() {
            setActiveChannel();
            assertThat(chatter.isActiveChannel(channel)).isTrue();
            assertThat(chatter.activeChannel()).isPresent().get().isEqualTo(channel);
        }

        @Test
        void then_view_is_updated_twice() {
            setActiveChannel();
            chatter.assertViewUpdated(2);
        }

        @Nested class given_chatter_already_joined_channel {
            @BeforeEach
            void setUp() {
                joinChannelBuilder(chatter, channel).execute();
                chatter.resetViewUpdate();
            }

            @Test
            void then_view_is_updated_once() {
                setActiveChannel();
                chatter.assertViewUpdated(1);
            }
        }

        @Nested class given_chatter_has_not_joined_channel {
            @Test
            void then_joins_chatter() {
                setActiveChannel();
                assertThat(chatter.isJoined(channel)).isTrue();
            }
        }

        @Nested class given_join_fails {
            @BeforeEach
            void setUp() {
                channel = channelWith(builder -> builder.policy(JoinChannelPolicy.class, DENY));
            }

            @Test
            void then_channel_is_not_set_active() {
                assertThat(setActiveChannel().wasFailure()).isTrue();
                assertThat(chatter.activeChannel()).isNotPresent();
            }
        }
    }
}
