package net.silthus.chat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Message {

    public static Message of(String message) {
        return new Message(null, message);
    }

    public static Message of(ChatSource source, String message) {
        return new Message(source, message);
    }

    ChatSource source;
    String message;
    @With Format format;
    ChatTarget target;

    private Message(ChatSource source, String message) {
        this(source, message, Format.defaultFormat(), ChatTarget.empty());
    }

    public Message withTarget(ChatTarget target) {
        if (target == null) return this;
        return new Message(getSource(), getMessage(), getFormat(), target);
    }

    public String formattedMessage() {
        return getFormat().applyTo(this);
    }

    public void send() {
        getTarget().sendMessage(this);
    }
}
