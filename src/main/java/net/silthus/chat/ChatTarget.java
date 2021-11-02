package net.silthus.chat;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface ChatTarget {

    static ChatTarget player(Player player) {
        return Chatter.of(player);
    }

    static ChatTarget nil() {
        return new NilChatTarget();
    }

    // TODO: test
    static Channel channel(String channelName) {
        return new Channel(channelName);
    }

    static Console console() {
        return Console.instance();
    }

    default void sendMessage(String message) {
        sendMessage(Message.message(message).withFormat(Format.directMessage()));
    }

    String getIdentifier();

    Message sendMessage(Message message);

    Message getLastReceivedMessage();

    Collection<Message> getReceivedMessages();

    Collection<Channel> getSubscriptions();

    void subscribe(Channel channel);

    void unsubscribe(Channel channel);
}
