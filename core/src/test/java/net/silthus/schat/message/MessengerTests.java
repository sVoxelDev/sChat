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
                Message message = draft.create();
                assertThat(draft.send()).isEqualTo(message);
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
                    draft.send();
                    assertThat(sendMessageCalled).isTrue();
                }
            }
        }
    }
}
