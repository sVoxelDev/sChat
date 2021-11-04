package net.silthus.chat.protocollib;

import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.silthus.chat.Chatter;
import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChatPacketQueueTest extends TestBase {

    private ChatPacketQueue listener;
    private Chatter chatter;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        listener = new ChatPacketQueue(plugin);
        chatter = Chatter.of(server.addPlayer());
    }

    @Test
    void create() {
        assertThat(listener.getQueuedMessages()).isEmpty();
    }

    @Test
    void getQueuedMessage_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> listener.getQueuedMessages().add(Message.message().build()));
    }

    @Test
    void addMessage_addsMessageToQueue() {

        Message message = message();
        listener.queueMessage(message);
        assertThat(listener.getQueuedMessages()).contains(message);
    }

    @Test
    void addMessage_returnsId() {
        Message message = message();
        UUID messageId = listener.queueMessage(message);

        assertThat(listener.getQueuedMessage(messageId))
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    void hasMessage_stringId_returnsTrue() {
        UUID id = listener.queueMessage(message());

        assertThat(listener.hasMessage(id.toString())).isTrue();
    }

    @Test
    void hasMessage_nullString_returnsFalse() {
        assertThat(listener.hasMessage(null)).isFalse();
    }

    @Test
    void hasMessage_noUUID_returnsFalse() {
        assertThat(listener.hasMessage("abc")).isFalse();
    }

    @Test
    void removeMessage_removesAndReturnsMessage() {
        Message message = message();
        UUID id = listener.queueMessage(message);

        assertThat(listener.removeMessage(id.toString()))
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    void removeMessage_nullId_returnsNull() {
        assertThat(listener.removeMessage(null)).isNull();
    }

    @Test
    void removeMessage_invalidUUID_returnsNull() {
        assertThat(listener.removeMessage("abc")).isNull();
    }

    @Test
    void removeTwice_returnsNull() {
        UUID id = listener.queueMessage(message());

        assertThat(listener.removeMessage(id.toString())).isNotNull();
        assertThat(listener.removeMessage(id.toString())).isNull();
    }

    @Test
    void onPacket_removesMessage_fromQueue() {


        Message message = message();
        listener.queueMessage(message);

        String messageJson = GsonComponentSerializer.gson().serialize(message.formatted());
        Gson gson = new Gson();
        Map messageMap = gson.fromJson(messageJson, HashMap.class);
        messageMap.put("schat_message_id", message.getId().toString());

        String finalJson = gson.toJson(messageMap);

        HashMap<?, ?> map = gson.fromJson(finalJson, HashMap.class);
        String id = (String) map.remove("schat_message_id");

        Component striped = GsonComponentSerializer.gson().deserialize(gson.toJson(map));
        assertThat(striped).isNotNull();
    }

    private Message message() {
        return Message.message("test").to(chatter).build();
    }
}