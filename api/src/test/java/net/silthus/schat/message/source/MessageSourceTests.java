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

package net.silthus.schat.message.source;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.identity.IdentityStub.randomIdentity;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MessageSource")
class MessageSourceTests {

    private Identity identity;
    private MessageSource source;

    @BeforeEach
    void setUp() {
        identity = randomIdentity();
        source = MessageSource.messageSource(identity);
    }

    @Nested
    @DisplayName("given Identity")
    class IdentifiedTest {

        @Test
        @DisplayName("has identity properties")
        void has_identity_properties() {
            assertThat(source).isNotNull()
                .extracting(
                    MessageSource::getId,
                    MessageSource::getName,
                    MessageSource::getDisplayName
                ).contains(
                    identity.getId(),
                    identity.getName(),
                    identity.getDisplayName()
                );
        }

        @Test
        @DisplayName("is not equal different identity")
        void is_not_equals() {
            assertThat(source).isNotEqualTo(MessageSource.messageSource(Identity.identity("test")));
        }
    }

    @Nested
    @DisplayName("given no Identity")
    class NoIdentityTest {

        @BeforeEach
        void setUp() {
            identity = Identity.nil();
            source = MessageSource.nil();
        }

        @Test
        @DisplayName("has random id")
        void has_random_id() {
            assertThat(source.getId()).isNotNull();
        }

        @Test
        @DisplayName("has empty name")
        void has_no_name() {
            assertThat(source.getName()).isEmpty();
        }

        @Test
        @DisplayName("has empty display name")
        void has_no_display_name() {
            assertThat(source.getDisplayName()).isEqualTo(Component.empty());
        }

        @Test
        @DisplayName("is equal other nil source")
        void equals() {
            assertThat(source).isEqualTo(MessageSource.nil());
        }
    }

    @Nested
    @DisplayName("message(...)")
    class SendMessage {

        @Test
        @DisplayName("creates message with source")
        void creates_message_with_source() {
            final TextComponent text = text("hey");
            final Message.Builder message = source.message(text);
            assertThat(message).extracting(
                Message.Builder::source,
                Message.Builder::text
            ).contains(
                source,
                text
            );
        }
    }
}
