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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import net.kyori.adventure.text.Component;
import net.silthus.schat.message.source.MessageSource;
import net.silthus.schat.message.target.MessageTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.message.Message.empty;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.message.MessageStub.createRandomMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.byLessThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@DisplayName("Message")
class MessageTests {

    @Nested
    @DisplayName("Builder")
    class Builder {

        private Message.Builder builder;

        @BeforeEach
        void setUp() {
            builder = Message.message();
        }

        @Test
        @DisplayName("targets are not null and empty")
        void targets_empty() {
            assertThat(builder.targets()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("targets are modifiable")
        void targets_are_modifiable() {
            builder.targets().add(mock(MessageTarget.class));
            assertThat(builder.targets()).isNotEmpty();
        }

        @Test
        @DisplayName("has empty text")
        void empty_text() {
            assertThat(builder.text()).isEqualTo(Component.empty());
        }

        @Test
        @DisplayName("has nil source")
        void nil_source() {
            assertThat(builder.source()).isEqualTo(MessageSource.nil());
        }
    }

    @Nested
    @DisplayName("create")
    class CreateMessage {

        private Message message;

        @BeforeEach
        void setUp() {
            message = createRandomMessage();
        }

        @Test
        @DisplayName("has unique id")
        void has_unique_id() {
            assertThat(message.getId()).isNotNull();
            assertThat(message.getId()).isNotEqualTo(createRandomMessage().getId());
        }

        @Test
        @DisplayName("has timestamp")
        void has_timestamp() {
            assertThat(message.getTimestamp()).isCloseTo(Instant.now(), byLessThan(100L, ChronoUnit.MILLIS));
        }

        @Test
        @DisplayName("has empty targets")
        void has_empty_targets() {
            assertThat(message.getTargets()).isEmpty();
        }

        @Test
        @DisplayName("targets are immutable")
        @SuppressWarnings("ConstantConditions")
        void targets_are_immutable() {
            assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> message.getTargets().add(mock(MessageTarget.class)));
        }

        @Nested
        @DisplayName("given no source")
        class NilSource {

            @BeforeEach
            void setUp() {
                message = message(text("test")).build();
            }

            @Test
            @DisplayName("has nil source")
            void has_nil_source() {
                assertThat(message.getSource()).isEqualTo(MessageSource.nil());
            }
        }
    }

    @Nested
    @DisplayName("empty()")
    class Empty {

        @Test
        @DisplayName("has nil source and empty text")
        void has_nil_source_and_empty_text() {
            assertThat(empty())
                .extracting(
                    Message::getSource,
                    Message::getText
                ).contains(
                    MessageSource.nil(),
                    Component.empty()
                );
        }
    }

    @Nested
    @DisplayName("send(...)")
    class Send {

        @Test
        @DisplayName("sends message to messenger")
        void delivers_message_to_messenger() {
            final Messenger spy = spy(Messenger.class);
            message().send(spy);
            verify(spy).sendMessage(any());
        }
    }
}
