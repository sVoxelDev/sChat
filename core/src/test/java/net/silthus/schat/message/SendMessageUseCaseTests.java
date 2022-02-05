package net.silthus.schat.message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.events.message.SendChannelMessageEvent;
import net.silthus.schat.events.message.SendMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.GLOBAL;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class SendMessageUseCaseTests {

    private SendMessage messenger;
    private ChannelRepository repository;

    @BeforeEach
    void setUp() {
        repository = createInMemoryChannelRepository();
        messenger = SendMessage.sendMessageUseCase().channelRepository(repository).create();
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

    @Nested class private_message {
        private @NotNull ChatterMock source;
        private @NotNull ChatterMock target;

        @BeforeEach
        void setUp() {
            source = randomChatter();
            target = randomChatter();
        }

        private Message sendPrivateMessage() {
            return send(message().source(source).to(target));
        }

        private String sourceId() {
            return source.uniqueId().toString();
        }

        private String targetId() {
            return target.uniqueId().toString();
        }

        private Component sourceName() {
            return source.displayName();
        }

        private Component targetName() {
            return target.displayName();
        }

        private Channel targetChannel() {
            return target.channel(sourceId()).orElseThrow();
        }

        private Channel sourceChannel() {
            return source.channel(targetId()).orElseThrow();
        }

        @Test
        void creates_channel_with_partner_name() {
            sendPrivateMessage();
            source.assertJoinedChannel(targetId(), targetName());
            target.assertJoinedChannel(sourceId(), sourceName());
        }

        @Test
        void private_channels_are_linked() {
            sendPrivateMessage();
            assertThat(sourceChannel().targets()).contains(targetChannel());
            assertThat(targetChannel().targets()).contains(sourceChannel());
        }

        @Test
        void private_channels_are_global() {
            sendPrivateMessage();
            assertThat(sourceChannel().is(GLOBAL)).isTrue();
        }

        @Test
        void target_receives_message() {
            final Message message = sendPrivateMessage();
            target.assertReceivedMessage(message);
        }

        @Test
        void private_channels_are_added_to_repository() {
            sendPrivateMessage();
            assertThat(repository.all()).contains(sourceChannel(), targetChannel());
        }

        @Nested class given_private_channel_exists {
            private Channel channel;

            @BeforeEach
            void setUp() {
                channel = Channel.channel(targetId()).create();
                repository.add(channel);
            }

            @Test
            void channel_is_reused() {
                final Message message = sendPrivateMessage();
                assertThat(channel).isSameAs(sourceChannel());
                assertThat(channel.messages()).contains(message);
            }
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
            messenger = SendMessage.sendMessageUseCase().eventBus(eventBus).create();
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
