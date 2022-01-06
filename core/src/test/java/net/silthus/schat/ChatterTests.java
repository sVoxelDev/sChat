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
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Messenger;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ChatterTests {

    private static final String MESSAGE_TEXT = "Hi!";

    private Chatter chatter;

    @BeforeEach
    void setUp() {
        chatter = Chatter.createChatter();
    }

    private @NotNull Message createMessage() {
        return Message.message(chatter, MESSAGE_TEXT);
    }

    private Channel setActiveChannel() {
        final Channel channel = Channel.createChannel("test");
        chatter.setActiveChannel(channel);
        return channel;
    }

    @Nested
    class WhenChatterChats {

        private Message chat() {
            return chatter.chat(MESSAGE_TEXT);
        }

        @Test
        void sends_message_to_self() {
            setActiveChannel();
            final Message message = chat();
            assertThat(chatter.getMessages()).contains(message);
        }

        @Test
        void given_noActiveChannel_chatFails() {
            chatter.clearActiveChannel();
            assertThatExceptionOfType(Chatter.NoActiveChannel.class)
                .isThrownBy(this::chat);
        }

        @Test
        void given_activeChannel_sendsChatMessage_toChannel() {
            final Channel channel = setActiveChannel();
            Message message = chat();
            assertThat(channel.getMessages()).contains(message);
        }

    }

    @Nested
    class SendMessage {

        @Test
        void adds_receivedMessage() {
            final Message message = createMessage();
            chatter.sendMessage(message);
            assertThat(chatter.getMessages()).contains(message);
        }

        @Test
        @SuppressWarnings("unchecked")
        void updatesView() {
            final Messenger<Chatter> out = mock(Messenger.class);
            chatter = Chatter.chatter().messenger(out).create();
            final Message message = Message.message(MESSAGE_TEXT);
            chatter.sendMessage(message);
            verify(out).sendMessage(eq(message), any());
        }
    }

    @Test
    void given_no_activeChannel_getActiveChannel_returns_first_channel() {
        Channel channel = Channel.createChannel("test");
        chatter.join(channel);
        assertThat(chatter.getActiveChannel()).isPresent().get().isEqualTo(channel);
    }
}
