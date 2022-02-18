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
package net.silthus.schat.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.message.MessageHelper.randomMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
            final Message message = randomMessage();
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
            message1 = randomMessage();
            message2 = randomMessage();
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
            .isThrownBy(() -> Messages.of().add(randomMessage()));
    }

    @Nested
    @DisplayName("Messages.of(...)")
    class MessagesOf {

        @BeforeEach
        void setUp() {
            messages = Messages.of(randomMessage(), randomMessage());
        }

        @Test
        @DisplayName("contains initial messages")
        void of_has_initial_message() {
            assertThat(messages).hasSize(2);
        }

        @Test
        @DisplayName("is mutable")
        void is_mutable() {
            final Message message = randomMessage();
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
                .isThrownBy(() -> unmodifiable.add(randomMessage()));
        }

        @Test
        @DisplayName("is view of original message container")
        void is_view_of_original() {
            final Message message = randomMessage();
            messages.add(message);
            assertThat(unmodifiable).contains(message);
        }
    }
}
