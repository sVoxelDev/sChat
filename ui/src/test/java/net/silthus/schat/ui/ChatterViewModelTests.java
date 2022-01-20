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

package net.silthus.schat.ui;

import lombok.SneakyThrows;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.PRIORITY;
import static net.silthus.schat.channel.ChannelHelper.ConfiguredSetting.set;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.ui.ViewModel.of;
import static org.assertj.core.api.Assertions.assertThat;

class ChatterViewModelTests {

    private Chatter chatter;
    private ViewModel model;

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
    private Message.Draft createMessage() {
        final Message.Draft one = Message.message().to(chatter);
        Thread.sleep(1L);
        return one;
    }

    private void sendMessage(Message message) {
        chatter.sendMessage(message);
    }

    @Nested class given_empty_view_model {

        @Test
        void messages_are_empty() {
            assertThat(model.getMessages()).isEmpty();
        }

        @Test
        void channels_are_empty() {
            assertThat(model.getChannels()).isEmpty();
        }
    }

    @Nested class given_user_with_messages {
        private Message one;
        private Message two;

        @BeforeEach
        void setUp() {
            one = createMessage();
            two = createMessage();
            sendMessage(two);
            sendMessage(one);
        }

        @Test
        void then_messages_are_sorted_by_time() {
            assertThat(model.getMessages()).containsExactly(
                one,
                two
            );
        }

        @Nested class given_message_sent_to_channel {
            private Channel channel;
            private Message.Draft message;

            @BeforeEach
            void setUp() {
                channel = randomChannel();
                message = createMessage().to(channel);
                sendMessage(message);
            }

            @Nested class given_channel_is_inactive {
                @Test
                void then_message_is_not_displayed() {
                    assertThat(model.getMessages()).doesNotContain(message);
                }
            }

            @Nested class given_channel_is_active {
                @BeforeEach
                void setUp() {
                    chatter.setActiveChannel(channel);
                }

                @Test
                void then_message_is_displayed() {
                    assertThat(model.getMessages()).contains(message);
                }
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
            assertThat(model.getChannels()).containsExactly(
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
                assertThat(model.getChannels()).containsExactly(
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
