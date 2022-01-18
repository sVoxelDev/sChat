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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.silthus.schat.identity.Identity;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.IdentityHelper.randomIdentity;
import static net.silthus.schat.message.MessengerTests.MessageOutMock.MessageOutAssert.to;
import static org.assertj.core.api.Assertions.assertThat;

class MessengerTests {

    private Messenger interactor;
    private MessageOutMock messageOut;
    private TextComponent text;

    @BeforeEach
    void setUp() {
        messageOut = new MessageOutMock();
        interactor = new Messenger().out(messageOut);
        text = Component.text("Hi");
    }

    @Nested class send {

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_target_throws_npe() {
            assertNPE(() -> interactor.sendMessageTo(Component.empty(), (Identity) null));
        }

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_message_throws_npe() {
            assertNPE(() -> interactor.sendMessageTo(null, randomIdentity()));
        }

        @Nested class given_one_target {
            private Identity identity;

            @BeforeEach
            void setUp() {
                identity = randomIdentity();
            }

            private void sendMessage() {
                interactor.sendMessageTo(text, identity);
            }

            private void assertMessageSent() {
                messageOut.assertMessageSent(to(identity, text));
            }

            @Test
            void then_delivers_message_to_target_using_output() {
                sendMessage();
                assertMessageSent();
            }

            @Test
            void then_message_has_unique_id() {
                sendMessage();
                assertThat(messageOut.getLastMessage())
                    .isNotNull().extracting(NewMessage::getId)
                    .isNotNull();
            }
        }

        @Nested class given_multiple_targets {
            private List<Identity> targets;

            @BeforeEach
            void setUp() {
                targets = new ArrayList<>(List.of(randomIdentity(), randomIdentity()));
            }

            private void sendMessageToAll() {
                interactor.sendMessageTo(text, targets);
            }

            private void assertMessageSentToAll() {
                messageOut.assertMessageSent(to(targets, text));
            }

            @Test
            void then_delivers_message_to_all() {
                sendMessageToAll();
                assertMessageSentToAll();
            }

            @Nested class when_targets_contains_null {
                @BeforeEach
                void setUp() {
                    targets.add(null);
                }

                @Test
                void then_target_is_ignored() {
                    sendMessageToAll();
                    assertMessageSentToAll();
                }
            }
        }
    }

    @Getter
    public static class MessageOutMock implements MessageOut {
        private static final PlainTextComponentSerializer SERIALIZER = PlainTextComponentSerializer.plainText();

        private final List<String> calls = new ArrayList<>();
        private final Queue<NewMessage> messages = new ArrayDeque<>();

        @Override
        public void onMessageSent(NewMessage newMessage) {
            messages.add(newMessage);
            calls.add(toSpyFormat(newMessage.getTarget(), newMessage.getText()));
        }

        private NewMessage getLastMessage() {
            return messages.peek();
        }

        public void assertMessageSent(MessageOutAssert... asserts) {
            final List<String> formattedAsserts = Arrays.stream(asserts).map(this::toSpyFormat).toList();
            assertThat(calls).isEqualTo(formattedAsserts);
        }

        @NotNull
        private String toSpyFormat(MessageOutAssert messageOutAssert) {
            return toSpyFormat(messageOutAssert.identity(), messageOutAssert.text());
        }

        @NotNull
        private String toSpyFormat(Identity target, Component message) {
            return target.getName() + "(" + target.getUniqueId() + "):" + SERIALIZER.serialize(message);
        }

        public record MessageOutAssert(Identity identity, Component text) {
            public static MessageOutAssert to(Identity identity, Component text) {
                return new MessageOutAssert(identity, text);
            }

            public static MessageOutAssert[] to(Collection<Identity> identities, Component text) {
                return identities.stream()
                    .filter(Objects::nonNull)
                    .map(identity -> new MessageOutAssert(identity, text))
                    .toArray(MessageOutAssert[]::new);
            }
        }
    }
}
