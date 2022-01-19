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
import net.kyori.adventure.text.TextComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.message.Message.message;
import static org.assertj.core.api.Assertions.assertThat;

class MessengerTests {

    private MessengerImpl messenger;
    private TextComponent text;

    @BeforeEach
    void setUp() {
        messenger = new MessengerImpl();
        text = Component.text("Hi");
    }

    @Nested class process {
        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_draft_throws_npe() {
            assertNPE(() -> messenger.process(null));
        }

        @Test
        void given_draft_returns_draft() {
            final Message.Draft draft = message();
            assertThat(messenger.process(draft)).isSameAs(draft);
        }
    }

    @Nested class deliver {
        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_message_throws_npe() {
            assertNPE(() -> messenger.deliver(null));
        }

        // TODO - tests to write:
        // - multiple targets
        // - querying targets
        // - modifying targets after send not possible

        @Nested class given_valid_message {
            private MessageImpl.Draft draft;

            @BeforeEach
            void setUp() {
                draft = MessageImpl.builder();
            }

            @Test
            void then_message_is_returned_by_deliver() {
                Message message = draft.build();
                assertThat(draft.send(messenger)).isEqualTo(message);
            }

            @Nested class given_one_target {
                private MessageTarget target;
                private boolean sendMessageCalled = false;

                @BeforeEach
                void setUp() {
                    target = message -> sendMessageCalled = true;
                    draft.to(target);
                }

                @Test
                void then_sends_message_to_target() {
                    draft.send(messenger);
                    assertThat(sendMessageCalled).isTrue();
                }
            }
        }
    }
}
