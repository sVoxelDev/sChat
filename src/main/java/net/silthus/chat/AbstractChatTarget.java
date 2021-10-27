package net.silthus.chat;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

public abstract class AbstractChatTarget implements ChatTarget {

    private final Queue<Message> receivedMessages = new LinkedTransferQueue<>();

    @Override
    public Message getLastReceivedMessage() {
        return receivedMessages.peek();
    }

    @Override
    public Collection<Message> getReceivedMessages() {
        return List.copyOf(receivedMessages);
    }

    protected void addReceivedMessage(Message lastMessage) {
        this.receivedMessages.add(lastMessage);
    }
}
