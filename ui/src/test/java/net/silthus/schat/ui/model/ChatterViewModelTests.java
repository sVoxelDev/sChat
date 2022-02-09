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

package net.silthus.schat.ui.model;

import lombok.SneakyThrows;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelSettings.PRIORITY;
import static net.silthus.schat.channel.ChannelHelper.ConfiguredSetting.set;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.ui.model.ChatterViewModel.of;
import static org.assertj.core.api.Assertions.assertThat;

class ChatterViewModelTests {

    private Chatter chatter;
    private ChatterViewModel model;

    @BeforeEach
    void setUp() {
        chatter = randomChatter();
        model = of(chatter);
    }

    private Channel join(Channel channel) {
        chatter.join(channel);
        return channel;
    }

    @SneakyThrows
    @NotNull
    private Message.Draft message() {
        final Message.Draft one = Message.message().to(chatter);
        Thread.sleep(1L);
        return one;
    }

    private Message sendMessage(Message.Draft draft) {
        Message message = draft.create();
        chatter.sendMessage(message);
        return message;
    }

    @Nested class given_empty_view_model {

        @Test
        void messages_are_empty() {
            assertThat(model.messages()).isEmpty();
        }

        @Test
        void channels_are_empty() {
            assertThat(model.channels()).isEmpty();
        }
    }

    @Nested class given_user_with_messages {
        private Message one;
        private Message two;

        @BeforeEach
        void setUp() {
            Message.Draft draftOne = message();
            Message.Draft draftTwo = message();
            two = sendMessage(draftTwo);
            one = sendMessage(draftOne);
        }

        @Test
        void then_messages_are_sorted_by_time() {
            assertThat(model.messages()).containsExactly(
                one,
                two
            );
        }

        @Nested class given_message_sent_to_channel {
            private Message message;

            @BeforeEach
            void setUp() {
                message = sendMessage(message().to(randomChannel()));
            }

            @Test
            void then_message_is_listed_in_messages() {
                assertThat(model.messages()).contains(message);
            }
        }
    }

    @Nested class given_user_joined_channels {

        private Channel two;
        private Channel one;

        @BeforeEach
        void setUp() {
            two = join(channelWith("efg"));
            one = join(channelWith("abc"));
        }

        @Test
        void channels_are_sorted_by_name() {
            assertThat(model.channels()).containsExactly(
                one,
                two
            );
        }

        @Nested class with_priority {

            private Channel second;
            private Channel first;
            private Channel third;

            @BeforeEach
            void setUp() {
                second = join(channelWith("ab", set(PRIORITY, 10)));
                first = join(channelWith("def", set(PRIORITY, 5)));
                third = join(channelWith("bcd", set(PRIORITY, 10)));
            }

            @Test
            void channels_are_sorted_by_priority_and_than_name() {
                assertThat(model.channels()).containsExactly(
                    first,
                    second,
                    third,
                    one,
                    two
                );
            }
        }
    }
}
