package net.silthus.chat;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

public abstract class AbstractChatTarget implements ChatTarget {

    private final Stack<Message> receivedMessages = new Stack<>();

    @Override
    public Message getLastReceivedMessage() {
        if (receivedMessages.isEmpty())
            return null;
        return receivedMessages.peek();
    }

    @Override
    public Collection<Message> getReceivedMessages() {
        return List.copyOf(receivedMessages);
    }

    protected void setLastMessage(Message lastMessage) {
        this.receivedMessages.push(lastMessage);
    }
}
