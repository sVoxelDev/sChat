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
import static net.silthus.schat.MessageHelper.randomText;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.chatter.ChatterProviderStub.chatterProviderStub;
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
