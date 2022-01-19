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
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.empty;
import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.IdentityHelper.randomIdentity;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.message.MessengerTests.MessageOutMock.MessageOutAssert.to;
import static net.silthus.schat.message.NewMessage.message;
import static org.assertj.core.api.Assertions.assertThat;

class MessengerTests {

    private MessengerImpl messenger;
    private MessageOutMock messageOut;
    private TextComponent text;

    @BeforeEach
    void setUp() {
        messageOut = new MessageOutMock();
        messenger = new MessengerImpl().out(messageOut);
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
            final NewMessage.Draft draft = message();
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
        // - message returned by deliver is message sent to target
        // - multiple targets
        // - querying targets
        // - modifying targets after send not possible

        @Nested class given_valid_message {
            private NewMessageImpl.Draft draft;

            @BeforeEach
            void setUp() {
                draft = NewMessageImpl.builder();
            }

            @Nested class given_one_target {
                private Chatter target;

                @BeforeEach
                void setUp() {
                    target = randomChatter();
                    draft.to(target);
                }

                @Test
                void then_delivers_message_to_chatter() {
                    draft.send(messenger);
                    messageOut.assertMessageSent(to(target, empty()));
                }
            }
        }
    }

    @Disabled
    @Nested class send {

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_target_throws_npe() {
            assertNPE(() -> messenger.sendMessageTo(empty(), (Identity) null));
        }

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_message_throws_npe() {
            assertNPE(() -> messenger.sendMessageTo(null, randomIdentity()));
        }

        @Nested class given_one_target {
            private Identity identity;

            @BeforeEach
            void setUp() {
                identity = randomIdentity();
            }

            private void sendMessage() {
                messenger.sendMessageTo(text, identity);
            }

            private void assertMessageSent() {
                messageOut.assertMessageSent(to(identity, text));
            }

            @Test
            void then_delivers_message_to_target_using_output() {
                sendMessage();
                assertMessageSent();
            }
        }

        @Nested class given_multiple_targets {
            private List<Identity> targets;

            @BeforeEach
            void setUp() {
                targets = new ArrayList<>(List.of(randomIdentity(), randomIdentity()));
            }

            private void sendMessageToAll() {
                messenger.sendMessageTo(text, targets);
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

        @Nested class given_single_channel_target {
            private Channel channel;

            @BeforeEach
            void setUp() {
                channel = randomChannel();
            }

            private void sendMessageToChannel() {
                messenger.sendMessageTo(text, channel);
            }

            @Test
            void given_channel_has_no_targets_then_out_is_not_called() {
                sendMessageToChannel();
                assertThat(messageOut.isOnMessageSentCalled()).isFalse();
            }

            @Nested class given_channel_has_single_target {
                private Chatter target;

                @BeforeEach
                void setUp() {
                    target = randomChatter();
                    channel.addTarget(target);
                }

                @Test
                void then_message_is_sent_to_target() {
                    sendMessageToChannel();
                    messageOut.assertMessageSent(to(target.getIdentity(), text));
                }
            }
        }
    }

    @Getter
    public static class MessageOutMock implements MessageOut {
        private static final PlainTextComponentSerializer SERIALIZER = PlainTextComponentSerializer.plainText();

        private final List<String> calls = new ArrayList<>();
        private final Queue<NewMessage> messages = new ArrayDeque<>();
        private boolean onMessageSentCalled = false;

        @Override
        public void onMessageSent(NewMessage newMessage) {
            onMessageSentCalled = true;
            messages.add(newMessage);
            calls.add(toSpyFormat(newMessage.target(), newMessage.text()));
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
