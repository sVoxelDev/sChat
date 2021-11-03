package net.silthus.chat;

import lombok.NonNull;

import java.util.*;
import java.util.concurrent.LinkedTransferQueue;

public abstract class AbstractChatTarget implements ChatTarget {

    private final Queue<Message> receivedMessages = new LinkedTransferQueue<>();
    private final Set<ChannelSubscription> subscriptions = new HashSet<>();

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
    public Collection<ChannelSubscription> getSubscriptions() {
        return List.copyOf(subscriptions);
    }

    @Override
    public ChannelSubscription subscribe(@NonNull Channel channel) {
        channel.add(this);
        ChannelSubscription subscription = new ChannelSubscription(channel, this);
        subscriptions.add(subscription);
        return subscription;
    }

    @Override
    public void unsubscribe(@NonNull Channel channel) {
        channel.remove(this);
        subscriptions.removeIf(subscription -> subscription.getChannel().equals(channel));
    }
}
