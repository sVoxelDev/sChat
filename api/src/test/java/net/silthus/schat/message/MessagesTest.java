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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.message.MessageStub.createRandomMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("Messages")
class MessagesTest {

    private Messages messages;

    @BeforeEach
    void setUp() {
        messages = new Messages();
    }

    @Test
    @DisplayName("is empty")
    void is_empty() {
        assertThat(messages).isEmpty();
    }

    @Nested
    @DisplayName("given message")
    class GivenMessage {

        @Test
        @DisplayName("adds message")
        void add_adds_message() {
            final Message message = createRandomMessage();
            messages.add(message);
            assertThat(messages).hasSize(1)
                .containsExactly(message);
        }
    }

    @Nested
    @DisplayName("given multiple messages")
    class GivenMultipleMessages {

        private Message message1;
        private Message message2;

        @BeforeEach
        void setUp() {
            message1 = createRandomMessage();
            message2 = createRandomMessage();
            messages.add(message1);
            messages.add(message2);
        }

        @Test
        @DisplayName("retains order")
        void add_multiple_retains_order() {
            assertThat(messages).hasSize(2)
                .containsExactly(message1, message2);
        }

        @Test
        @DisplayName("does not add duplicates")
        void add_does_not_add_duplicates() {
            assertThat(messages.add(message1)).isFalse();
            assertThat(messages).hasSize(2)
                .containsOnlyOnce(message1);
        }

        @Test
        @DisplayName("last() returns last added message")
        void last_returns_last_added_message() {
            assertThat(messages.last()).isEqualTo(message2);
        }
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    @DisplayName("Messages.of() is unmodifiable")
    void of_is_unmodifiable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> Messages.of().add(createRandomMessage()));
    }

    @Nested
    @DisplayName("Messages.of(...)")
    class MessagesOf {

        @BeforeEach
        void setUp() {
            messages = Messages.of(createRandomMessage(), createRandomMessage());
        }

        @Test
        @DisplayName("contains initial messages")
        void of_has_initial_message() {
            assertThat(messages).hasSize(2);
        }

        @Test
        @DisplayName("is mutable")
        void is_mutable() {
            final Message message = createRandomMessage();
            messages.add(message);
            assertThat(messages).contains(message);
        }
    }

    @Nested
    @DisplayName("Messages.unmodifiable(...)")
    class Unmodifiable {

        private Messages unmodifiable;

        @BeforeEach
        void setUp() {
            unmodifiable = Messages.unmodifiable(messages);
        }

        @Test
        @DisplayName("is unmodifiable")
        void is_unmodifiable() {
            assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> unmodifiable.add(createRandomMessage()));
        }

        @Test
        @DisplayName("is view of original message container")
        void is_view_of_original() {
            final Message message = createRandomMessage();
            messages.add(message);
            assertThat(unmodifiable).contains(message);
        }
    }
}
