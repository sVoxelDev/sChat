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
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTargetSpy;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.message.MessageHelper.randomText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChatCommandTest {

    private ChatterMock chatter;
    private MessageTargetSpy target;

    @BeforeEach
    void setUp() {
        chatter = randomChatter();
        target = new MessageTargetSpy();
    }

    @NotNull
    private Message chat() {
        return ChatCommand.chat(chatter, randomText());
    }

    @Nested class given_no_active_channel {
        @BeforeEach
        void setUp() {
            chatter.activeChannel(null);
        }

        @Test
        void then_chat_throws() {
            assertThatExceptionOfType(ChatCommand.NoActiveChannel.class)
                .isThrownBy(ChatCommandTest.this::chat);
        }
    }

    @Nested
    class given_active_channel {

        @BeforeEach
        void setUp() {
            Channel activeChannel = randomChannel();
            activeChannel.addTarget(target);
            chatter.activeChannel(activeChannel);
        }

        @Test
        void then_sends_message_to_channel() {
            final Message message = chat();
            target.assertReceivedMessage(message);
        }

        @Test
        void then_sets_message_source_to_chatter_identity() {
            assertThat(chat().source()).isEqualTo(chatter);
        }

        @Test
        void then_sets_message_type_to_chat() {
            assertThat(chat().type()).isEqualTo(Message.Type.CHAT);
        }
    }
}
