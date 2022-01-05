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

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.ChannelHelper.channelWith;
import static net.silthus.schat.IdentityHelper.randomIdentity;
import static net.silthus.schat.message.Message.message;
import static org.assertj.core.api.Assertions.assertThat;

class ChatterRendererModelTests {

    private Chatter chatter;
    private ChatterViewModel viewModel;

    @BeforeEach
    void setUp() {
        chatter = Chatter.chatter(randomIdentity()).permissionHandler(permission -> true).create();
        viewModel = new ChatterViewModel(chatter);
    }

    @NotNull
    private Channel joinChannel(String name) {
        return joinChannel(Channel.createChannel(name));
    }

    private Channel joinChannel(Channel channel) {
        chatter.join(channel);
        return channel;
    }

    @Test
    void given_empty_lists_empty_channels() {
        assertThat(viewModel.getChannels()).isEmpty();
    }

    @Test
    void given_channel_is_listed_in_view_model() {
        final Channel channel = joinChannel("test");
        assertThat(viewModel.getChannels()).contains(new ChannelViewModel(channel));
    }

    @Test
    void given_two_channels_sorts_by_name() {
        final Channel second = joinChannel("mij");
        final Channel first = joinChannel("abc");
        assertThat(viewModel.getChannels()).containsExactly(
            new ChannelViewModel(first),
            new ChannelViewModel(second)
        );
    }

    @Test
    void given_two_channels_public_is_listed_first() {
        final Channel protectedChannel = joinChannel(channelWith("abc", Channel.REQUIRES_JOIN_PERMISSION, true));
        final Channel publicChannel = joinChannel("public");
        assertThat(viewModel.getChannels()).containsExactly(
            new ChannelViewModel(publicChannel),
            new ChannelViewModel(protectedChannel)
        );
    }

    @Test
    void given_no_messages_returns_empty_list() {
        assertThat(viewModel.getMessages()).isEmpty();
    }

    @Test
    void given_one_message_returns_message() {
        final Message message = message("Hi");
        chatter.sendMessage(message);
        assertThat(viewModel.getMessages()).containsExactly(new MessageViewModel(message));
    }

    @Test
    void given_two_messages_sorts_by_timestamp() throws InterruptedException {
        final Message one = message("one");
        Thread.sleep(1L);
        final Message two = message("two");
        Thread.sleep(1L);
        chatter.sendMessage(two);
        chatter.sendMessage(one);

        assertThat(viewModel.getMessages()).containsExactly(
            new MessageViewModel(one),
            new MessageViewModel(two)
        );
    }

    @Test
    void given_deleted_message_hides_deleted_message() {
        final Message deleted = message("deleted");
        chatter.sendMessage(deleted);
        deleted.setDeleted(true);

        assertThat(viewModel.getMessages()).isEmpty();
    }
}
