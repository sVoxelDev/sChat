package net.silthus.schat.message;

import java.util.LinkedList;
import java.util.Queue;
import lombok.NonNull;

import static org.assertj.core.api.Assertions.assertThat;

public class MockTarget implements MessageTarget {

    private final Queue<Message> messages = new LinkedList<>();

    @Override
    public void sendMessage(@NonNull Message message) {
        this.messages.add(message);
    }

    public void assertLastMessageIs(Message message) {
        assertThat(messages.peek()).isEqualTo(message);
    }

    public void assertReceivedMessage(Message message) {
        assertThat(messages).contains(message);
    }

    public void assertReceivedMessage() {
        assertThat(messages).isNotEmpty();
    }

    public void assertReceiveNoMessages() {
        assertThat(messages).isEmpty();
    }

    public void assertReceivedMessageCountIs(int count) {
        assertThat(messages).hasSize(count);
    }
}
