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
import net.silthus.schat.message.Message;
import net.silthus.schat.ui.ViewModel;
import net.silthus.schat.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.ChannelHelper.ConfiguredSetting.set;
import static net.silthus.schat.ChannelHelper.channelWith;
import static net.silthus.schat.ChannelHelper.randomChannel;
import static net.silthus.schat.UserHelper.randomUser;
import static net.silthus.schat.channel.Channel.PRIORITY;
import static net.silthus.schat.message.Message.message;
import static org.assertj.core.api.Assertions.assertThat;

class ViewModelTests {

    private User user;
    private ViewModel model;

    @BeforeEach
    void setUp() {
        user = randomUser();
        model = new ViewModel(user);
    }

    @Test
    void givenEmptyViewModel_getMessages_isEmpty() {
        assertThat(model.getMessages()).isEmpty();
    }

    @Test
    void givenUserWithMessages_getMessages_listsMessages() {
        final Message message = message().to(user).send();
        assertThat(model.getMessages()).contains(message);
    }

    @Test
    void givenUnsortedMessages_getMessages_sortsByTime() throws InterruptedException {
        final Message one = message().to(user).create();
        Thread.sleep(1L);
        final Message two = message().to(user).create();
        user.sendMessage(two);
        user.sendMessage(one);
        assertThat(model.getMessages()).containsExactly(
            one,
            two
        );
    }

    @Test
    void givenEmptyViewModel_getChannels_isEmpty() {
        assertThat(model.getChannels()).isEmpty();
    }

    @Test
    void givenUserJoinedChannel_getChannels_listsChannels() {
        final Channel channel = randomChannel();
        user.addChannel(channel);
        assertThat(model.getChannels()).contains(channel);
    }

    @Test
    void givenMultipleChannels_getChannels_areSortedByName() {
        final Channel one = Channel.createChannel("abc");
        final Channel two = Channel.createChannel("efg");
        user.addChannel(two);
        user.addChannel(one);
        assertThat(model.getChannels()).containsExactly(
            one,
            two
        );
    }

    @Test
    void givenMultipleChannelsWithPriority_areSortedByPriorityThenName() {
        final Channel one = channelWith("def", set(PRIORITY, 5));
        final Channel two = channelWith("abc", set(PRIORITY, 10));
        final Channel three = channelWith("def", set(PRIORITY, 10));
        user.addChannel(two);
        user.addChannel(one);
        user.addChannel(three);
        assertThat(model.getChannels()).containsExactly(
            one,
            two,
            three
        );
    }
}
