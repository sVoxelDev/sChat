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

package net.silthus.schat.message;

import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.message.Message.emptyMessage;
import static net.silthus.schat.message.Message.message;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MessageTests {

    private void assertMessageSent(MessageTarget... targets) {
        final Message message = message().to(targets).send();
        assertThat(message).isNotNull();
        for (final MessageTarget target : targets) {
            verify(target).sendMessage(message);
        }
    }

    @Nested class given_empty_message {

        private Message message;

        @BeforeEach
        void setUp() {
            message = emptyMessage();
        }

        @Test
        void text_is_not_null() {
            assertThat(message.getText()).isEqualTo(Component.empty());
        }

        @Test
        void source_is_nil_source() {
            assertThat(message.getSource()).isEqualTo(Identity.nil());
        }

        @Test
        void send_does_nothing() {
            assertThatCode(() -> message.send()).doesNotThrowAnyException();
        }

        @Test
        void type_is_system() {
            assertThat(message.getType()).isEqualTo(Message.Type.SYSTEM);
        }
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void given_null_source_throws() {
        assertNPE(() -> message().source(null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void given_null_text_throws() {
        assertNPE(() -> message().text((Component) null));
        assertNPE(() -> message().text((String) null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void given_null_target_throws() {
        assertNPE(() -> message().to((MessageTarget[]) null));
        assertNPE(() -> message().to((MessageTarget) null));
    }

    @Test
    void given_message_with_channel_target_sends_message_to_channel() {
        assertMessageSent(mock(Channel.class));
    }

    @Test
    void given_message_with_two_channel_targets_sends_message_to_both() {
        assertMessageSent(mock(Channel.class), mock(Channel.class));
    }

    @Test
    void given_message_with_one_user_and_one_channel_sends_message_to_both() {
        assertMessageSent(mock(MessageTarget.class), mock(Channel.class));
    }
}
