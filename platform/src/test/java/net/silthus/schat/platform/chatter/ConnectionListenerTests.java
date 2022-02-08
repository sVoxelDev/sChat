package net.silthus.schat.platform.chatter;

import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.platform.messaging.MessagingServiceMock;
import net.silthus.schat.platform.sender.SenderMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static net.silthus.schat.identity.IdentityHelper.randomIdentity;
import static net.silthus.schat.platform.sender.SenderMock.randomSender;
import static org.assertj.core.api.Assertions.assertThat;

class ConnectionListenerTests {

    private ConnectionListener listener;
    private ChatterRepository chatterRepository;
    private SenderMock sender;
    private MessagingServiceMock messenger;

    @BeforeEach
    void setUp() {
        chatterRepository = createInMemoryChatterRepository();
        messenger = new MessagingServiceMock();
        listener = new ConnectionListener(chatterRepository, ChatterMock::randomChatter, messenger) {};
        sender = randomSender();
    }

    private ConnectionListener.ChatterJoined createRandomPluginMessage() {
        return new ConnectionListener.ChatterJoined(randomIdentity())
            .repository(chatterRepository)
            .factory(ChatterMock::randomChatter);
    }

    @Nested class onJoin {
        private void join() {
            listener.onJoin(sender);
        }

        @Test
        void loads_chatter_into_cache() {
            join();
            assertThat(chatterRepository.contains(sender.uniqueId())).isTrue();
        }

        @Test
        void sends_join_ping_to_all_servers() {
            join();
            messenger.assertSentMessage(ConnectionListener.ChatterJoined.class);
            messenger.assertLastReceivedMessage(ConnectionListener.ChatterJoined.class)
                .extracting(ConnectionListener.ChatterJoined::identity)
                .isEqualTo(sender.identity());
        }

        @Test
        void when_ping_is_processed_then_chatter_is_created() {
            final ConnectionListener.ChatterJoined msg = createRandomPluginMessage();
            messenger.consumeIncomingMessage(msg);
            assertThat(chatterRepository.contains(msg.identity().uniqueId())).isTrue();
        }
    }
}
