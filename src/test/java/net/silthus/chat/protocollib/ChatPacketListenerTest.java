package net.silthus.chat.protocollib;

import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChatPacketListenerTest extends TestBase {

    private ChatPacketListener listener;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        listener = new ChatPacketListener(plugin);
    }

    @Test
    void create() {
        assertThat(listener.getQueuedMessages()).isEmpty();
    }

    @Test
    void getQueuedMessage_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> listener.getQueuedMessages().put(UUID.randomUUID(), Message.message().build()));
    }

    @Test
    void addMessage_addsMessageToQueue() {

        Message message = message();
        listener.addMessage(message);
        assertThat(listener.getQueuedMessages()).containsValue(message);
    }

    @Test
    void addMessage_returnsId() {
        Message message = message();
        UUID messageId = listener.addMessage(message);

        assertThat(listener.getMessage(messageId))
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    void hasMessage_stringId_returnsTrue() {
        UUID id = listener.addMessage(message());

        assertThat(listener.hasMessage(id.toString())).isTrue();
    }

    private Message message() {
        return Message.message("test").build();
    }
}