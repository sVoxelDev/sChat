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

package net.silthus.schat.usecases;

import net.silthus.schat.MessageHelper;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Messenger;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChatListenerImplTest {

    private ChatListenerImpl listener;
    private Chatter chatter;
    private Message deliveredMessage;
    private boolean messageDelivered = false;

    @BeforeEach
    void setUp() {
        listener = new ChatListenerImpl().messenger(new Messenger() {
            @Override
            public Message.Draft process(Message.Draft message) {
                return message;
            }

            @Override
            public void deliver(Message message) {
                deliveredMessage = message;
                messageDelivered = true;
            }
        });
        chatter = randomChatter();
    }

    @NotNull
    private Message chat() {
        return listener.onChat(chatter, MessageHelper.randomText());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void given_null_inputs_throws() {
        assertNPE(() -> listener.onChat(null, null));
        assertNPE(() -> listener.onChat(chatter, null));
    }

    @Test
    void given_no_active_channel_throws() {
        assertThatExceptionOfType(ChatListener.NoActiveChannel.class)
            .isThrownBy(this::chat);
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
        void then_sets_message_source_to_user() {
            assertThat(chat().source()).isEqualTo(chatter);
        }

        @Test
        void then_sets_message_type_to_chat() {
            assertThat(chat().type()).isEqualTo(Message.Type.CHAT);
        }
    }
}
