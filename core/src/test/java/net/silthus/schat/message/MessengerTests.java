package net.silthus.schat.message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.events.message.SendChannelMessageEvent;
import net.silthus.schat.events.message.SendMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class MessengerTests {

    private Messenger messenger;

    @BeforeEach
    void setUp() {
        messenger = Messenger.messenger().create();
    }

    private Message send(Message.Draft draft) {
        return messenger.send(draft.create());
    }

    @Test
    void single_target_message_is_sent_to_target() {
        final MockTarget target = new MockTarget();
        final Message message = send(message().to(target));
        target.assertReceivedMessage(message);
    }

    @Test
    void same_message_is_only_sent_once() {
        final MockTarget target = new MockTarget();
        final Message message = send(message().to(target));
        messenger.send(message);
        target.assertReceivedMessageCountIs(1);
    }

    @Nested class private_messages {
        private @NotNull ChatterMock source;
        private @NotNull ChatterMock target;
        private Message.@NotNull Draft draft;

        @BeforeEach
        void setUp() {
            source = randomChatter();
            target = randomChatter();
            draft = message().source(source).to(target);
        }

        private Message sendPrivateMessage() {
            return send(draft);
        }

        @Test
        void creates_channel_with_partner_name() {
            sendPrivateMessage();
            assertThat(source.channels())
                .isNotEmpty()
                .allMatch(channel -> channel.key().equals(target.uniqueId().toString()));
        }
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
            messenger = Messenger.messenger().eventBus(eventBus).create();
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
            final MessageTarget target = mock(MessageTarget.class);

            final Message message = messenger.send(message().to(target).create());

            assertThat(eventCalled).isTrue();
            verify(target).sendMessage(message);
        }

        @Test
        void cancelled_event_prevents_message_sending() {
            onEvent(event -> event.cancelled(true));
            final MessageTarget target = mock(MessageTarget.class);

            messenger.send(message().to(target).create());

            verify(target, never()).sendMessage(any());
        }

        @Test
        void targets_are_modifiable_when_event_is_called() {
            final MessageTarget target = mock(MessageTarget.class);
            onEvent(event -> event.targets().add(target));

            final Message message = messenger.send(randomMessage());

            verify(target).sendMessage(message);
        }
    }
}
