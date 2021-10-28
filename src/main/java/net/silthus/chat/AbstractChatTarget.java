package net.silthus.chat;

import lombok.NonNull;

import java.util.*;
import java.util.concurrent.LinkedTransferQueue;

public abstract class AbstractChatTarget implements ChatTarget {

    private final Queue<Message> receivedMessages = new LinkedTransferQueue<>();
    private final Set<Channel> subscriptions = new HashSet<>();

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

    @Override
    public Collection<Channel> getSubscriptions() {
        return List.copyOf(subscriptions);
    }

    @Override
    public void subscribe(@NonNull Channel channel) {
        channel.add(this);
        subscriptions.add(channel);
    }

    @Override
    public void unsubscribe(@NonNull Channel channel) {
        channel.remove(this);
        subscriptions.remove(channel);
    }
}
