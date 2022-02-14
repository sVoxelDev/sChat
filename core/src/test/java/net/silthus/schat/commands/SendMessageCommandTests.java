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

package net.silthus.schat.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.events.message.SendChannelMessageEvent;
import net.silthus.schat.events.message.SendMessageEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.MockTarget;
import net.silthus.schat.message.Targets;
import net.silthus.schat.policies.SendChannelMessagePolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static net.silthus.schat.policies.SendChannelMessagePolicy.DENY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class SendMessageCommandTests {

    private SendMessageResult send(Message.Draft draft) {
        return draft.create().send();
    }

    @Test
    void single_target_message_is_sent_to_target() {
        final MockTarget target = new MockTarget();
        final Message message = message().to(target).send();
        target.assertReceivedMessage(message);
    }

    @Nested class given_channel {
        private Channel channel;
        private final List<MockTarget> targets = new ArrayList<>();

        @BeforeEach
        void setUp() {
            channel = randomChannel();
            addMockTarget();
            addMockTarget();
        }

        private MockTarget addMockTarget() {
            final MockTarget target = new MockTarget();
            targets.add(target);
            channel.addTarget(target);
            return target;
        }

        @Test
        void when_channel_is_targeted_then_message_is_sent_to_all_channel_targets() {
            send(message().to(channel));
            assertThat(targets).allSatisfy(MockTarget::assertReceivedMessage);
        }

        @Test
        void send_message_policy_of_channel_is_checked() {
            channel = channelWith(builder -> builder.policy(SendChannelMessagePolicy.class, DENY));
            SendMessageResult result = channel.sendMessage(message().to(channel).create());
            assertThat(result.wasFailure()).isTrue();
        }

        @Nested class events {
            private EventBusMock eventBus;
            private boolean calledEvent = false;

            @BeforeEach
            void setUp() {
                eventBus = new EventBusMock();
                channel = randomChannel();
            }

            @AfterEach
            void tearDown() {
                eventBus.close();
            }

            private void onEvent(Consumer<SendChannelMessageEvent> handler) {
                eventBus.on(SendChannelMessageEvent.class, handler);
            }

            @Test
            void sendMessage_fires_ChannelMessageEvent() {
                onEvent(event -> calledEvent = true);

                channel.sendMessage(randomMessage());

                assertThat(calledEvent).isTrue();
            }

            @Test
            void sendMessage_uses_modified_targets() {
                final MockTarget target = new MockTarget();
                onEvent(event -> event.targets(Targets.of(target)));

                channel.sendMessage(randomMessage());

                target.assertReceivedMessage();
            }

            @Test
            void cancelled_event_cancels_message_sending() {
                final MockTarget target = addMockTarget();
                onEvent(event -> event.cancelled(true));

                channel.sendMessage(randomMessage());

                target.assertReceiveNoMessages();
            }

            @Test
            void sendMessage_uses_message_of_event() {
                final MockTarget target = addMockTarget();
                final Message replacedMessage = randomMessage();
                onEvent(event -> event.message(replacedMessage));

                channel.sendMessage(randomMessage());

                target.assertReceivedMessage(replacedMessage);
            }
        }
    }

    @Nested
    class events {
        private EventBusMock eventBus;
        private boolean eventCalled = false;

        @BeforeEach
        void setUp() {
            eventBus = new EventBusMock();
            SendMessageCommand.prototype(builder -> builder.eventBus(eventBus));
            onEvent(event -> eventCalled = true);
        }

        @AfterEach
        void tearDown() {
            eventBus.close();
        }

        private void onEvent(Consumer<SendMessageEvent> event) {
            eventBus.on(SendMessageEvent.class, event);
        }

        @Test
        void send_calls_event() {
            ChatterMock target = randomChatter();

            final Message message = message().to(target).send();

            assertThat(eventCalled).isTrue();
            target.assertReceivedMessage(message);
        }

        @Test
        void cancelled_event_prevents_message_sending() {
            onEvent(event -> event.cancelled(true));
            final MessageTarget target = mock(MessageTarget.class);

            message().to(target).send();

            verify(target, never()).sendMessage(any());
        }

        @Test
        void targets_are_modifiable_when_event_is_called() {
            ChatterMock target = randomChatter();
            onEvent(event -> event.targets().add(target));

            final Message message = randomMessage().send().message();

            target.assertReceivedMessage(message);
        }
    }
}
