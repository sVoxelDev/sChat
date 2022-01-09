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
import net.silthus.schat.message.Message;
import net.silthus.schat.user.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.ChannelHelper.ConfiguredSetting.set;
import static net.silthus.schat.ChannelHelper.channelWith;
import static net.silthus.schat.ChannelHelper.randomChannel;
import static net.silthus.schat.UserHelper.randomUser;
import static net.silthus.schat.channel.Channel.PRIORITY;
import static org.assertj.core.api.Assertions.assertThat;

class ViewModelTests {

    private User user;
    private ViewModel model;

    @BeforeEach
    void setUp() {
        user = randomUser();
        model = new ViewModel(user);
    }

    private Channel join(Channel channel) {
        user.addChannel(channel);
        return channel;
    }

    @SneakyThrows
    @NotNull
    private Message createMessage() {
        final Message one = Message.message().to(user).create();
        Thread.sleep(1L);
        return one;
    }

    @NotNull
    private Message sendMessage() {
        return Message.message().to(user).send();
    }

    private void sendMessage(Message message) {
        user.sendMessage(message);
    }

    @Nested
    class GetMessagesTests {

        @Test
        void givenEmptyViewModel_getMessages_isEmpty() {
            assertThat(model.getMessages()).isEmpty();
        }

        @Test
        void givenUserWithMessages_getMessages_listsMessages() {
            final Message message = sendMessage();
            assertThat(model.getMessages()).contains(message);
        }

        @Test
        void givenUnsortedMessages_getMessages_sortsByTime() {
            final Message one = createMessage();
            final Message two = createMessage();
            sendMessage(two);
            sendMessage(one);
            assertThat(model.getMessages()).containsExactly(
                one,
                two
            );
        }
    }

    @Nested
    class GetChannelsTests {

        @Test
        void givenEmptyViewModel_getChannels_isEmpty() {
            assertThat(model.getChannels()).isEmpty();
        }

        @Test
        void givenUserJoinedChannel_getChannels_listsChannels() {
            final Channel channel = join(randomChannel());
            assertThat(model.getChannels()).contains(channel);
        }

        @Test
        void givenMultipleChannels_getChannels_areSortedByName() {
            final Channel two = join(channelWith("efg"));
            final Channel one = join(channelWith("abc"));
            assertThat(model.getChannels()).containsExactly(
                one,
                two
            );
        }

        @Test
        void givenMultipleChannelsWithPriority_areSortedByPriorityThenName() {
            final Channel two = join(channelWith("abc", set(PRIORITY, 10)));
            final Channel one = join(channelWith("def", set(PRIORITY, 5)));
            final Channel three = join(channelWith("bcd", set(PRIORITY, 10)));
            assertThat(model.getChannels()).containsExactly(
                one,
                two,
                three
            );
        }
    }
}
