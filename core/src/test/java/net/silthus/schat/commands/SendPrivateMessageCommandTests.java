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

import java.util.Set;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.channel.ChannelSettings.GLOBAL;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.commands.SendPrivateMessageCommand.sendPrivateMessageBuilder;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.message.MessageHelper.randomText;
import static org.assertj.core.api.Assertions.assertThat;

@Nested
class SendPrivateMessageCommandTests {
    private ChannelRepository repository;
    private @NotNull ChatterMock source;
    private @NotNull ChatterMock target;

    @BeforeEach
    void setUp() {
        repository = createInMemoryChannelRepository();
        CreatePrivateChannelCommand.prototype(builder -> builder.channelRepository(repository));
        SendMessageCommand.prototype(builder -> builder.eventBus(EventBus.empty()));
        source = randomChatter();
        target = randomChatter();
    }

    private Message send(Message.Draft draft) {
        return draft.send();
    }

    private Message sendPrivateMessage() {
        return sendPrivateMessageFrom(source);
    }

    private Message sendPrivateMessageFrom(Chatter source) {
        return send(message().source(source).to(target));
    }

    private String idOf(Chatter chatter) {
        return chatter.uniqueId().toString();
    }

    private String targetId() {
        return target.uniqueId().toString();
    }

    private Channel privateChannel() {
        return source.channels().stream().filter(channel -> channel.is(PRIVATE)).findFirst().orElseThrow();
    }

    @Test
    void creates_private_channel() {
        sendPrivateMessage();
        source.assertJoinedChannel(privateChannel());
        target.assertJoinedChannel(privateChannel());
    }

    @Test
    void private_channels_are_global() {
        sendPrivateMessage();
        assertThat(privateChannel().is(GLOBAL)).isTrue();
    }

    @Test
    void private_channels_have_private_setting() {
        sendPrivateMessage();
        assertThat(privateChannel().is(PRIVATE)).isTrue();
    }

    @Test
    void target_receives_message() {
        final Message message = sendPrivateMessage();
        target.assertReceivedMessage(message);
    }

    @Test
    void private_channels_are_added_to_repository() {
        sendPrivateMessage();
        assertThat(repository.contains(privateChannel())).isTrue();
    }

    @Test
    void private_channel_is_set_active() {
        sendPrivateMessage();
        source.assertActiveChannel(privateChannel());
    }

    @Test
    void given_setActive_is_false_then_private_channel_is_not_set_as_active() {
        final Channel channel = randomChannel();
        source.activeChannel(channel);
        sendPrivateMessageBuilder(source, target, randomText()).setActive(false).execute();
        assertThat(source.isActiveChannel(channel)).isTrue();
        source.assertJoinedChannel(privateChannel().key());
    }

    @Test
    void message_has_source_and_is_of_type_chat() {
        final Message message = sendPrivateMessage();
        assertThat(message.source()).isEqualTo(source);
        assertThat(message.type()).isEqualTo(Message.Type.CHAT);
    }

    @Nested
    class given_private_channel_exists {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = channelWith(Set.of(source, target).hashCode() + "", PRIVATE, true);
            channel.addTarget(source);
            channel.addTarget(target);
            repository.add(channel);
        }

        @Test
        void channel_is_reused() {
            final Message message = sendPrivateMessage();
            assertThat(channel).isSameAs(privateChannel());
            assertThat(channel.messages()).contains(message);
        }
    }
}
