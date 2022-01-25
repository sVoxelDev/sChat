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

package net.silthus.schat.platform.listener;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Messenger;
import net.silthus.schat.usecases.OnChat;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.chatter.ChatterProviderStub.chatterProviderStub;
import static net.silthus.schat.message.MessageHelper.randomText;
import static net.silthus.schat.platform.locale.Messages.CANNOT_CHAT_NO_ACTIVE_CHANNEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChatListenerTest {

    private ChatListener listener;
    private ChatterMock chatter;
    private Message deliveredMessage;
    private boolean messageDelivered = false;

    @BeforeEach
    void setUp() {
        chatter = randomChatter();
        listener = new ChatListener().messenger(new Messenger() {
            @Override
            public Message.Draft process(Message.Draft message) {
                return message;
            }

            @Override
            public void deliver(Message message) {
                deliveredMessage = message;
                messageDelivered = true;
            }
        }).chatterProvider(chatterProviderStub(chatter));
    }

    @NotNull
    private Message chat() {
        return listener.onChat(chatter, randomText());
    }

    private Component chatWithId() {
        final Component text = randomText();
        listener.onChat(chatter.getUniqueId(), text);
        return text;
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void given_null_inputs_throws() {
        assertNPE(() -> listener.onChat((Chatter) null, null));
        assertNPE(() -> listener.onChat(chatter, null));
        assertNPE(() -> listener.onChat((UUID) null, null));
        assertNPE(() -> listener.onChat(randomUUID(), null));
    }

    @Nested class given_no_active_channel {
        @BeforeEach
        void setUp() {
            chatter.setActiveChannel(null);
        }

        @Test
        void then_chat_throws() {
            assertThatExceptionOfType(OnChat.NoActiveChannel.class)
                .isThrownBy(ChatListenerTest.this::chat);
        }

        @Test
        void then_chat_with_id_sends_error_message() {
            chatWithId();
            chatter.assertReceivedMessage(CANNOT_CHAT_NO_ACTIVE_CHANNEL.build());
        }
    }

    @Nested
    class given_active_channel {

        @BeforeEach
        void setUp() {
            chatter.setActiveChannel(randomChannel());
        }

        @Test
        void then_sends_message_to_channel() {
            final Message message = chat();
            assertThat(messageDelivered).isTrue();
            assertThat(deliveredMessage).isEqualTo(message);
        }

        @Test
        void then_sets_message_source_to_chatter_identity() {
            assertThat(chat().source()).isEqualTo(chatter.getIdentity());
        }

        @Test
        void then_sets_message_type_to_chat() {
            assertThat(chat().type()).isEqualTo(Message.Type.CHAT);
        }

        @Test
        void when_chat_with_id_is_called_then_sends_message_to_chatters_channel() {
            final Component text = chatWithId();
            assertThat(deliveredMessage.text()).isEqualTo(text);
        }
    }
}
