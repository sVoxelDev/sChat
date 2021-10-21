package net.silthus.chat;

import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
public class Message {

    public static Message of(String message) {
        return new Message(null, message);
    }

    public static Message of(ChatSource source, String message) {
        return new Message(source, message);
    }

    ChatSource source;
    String message;
    boolean formatted;

    public Message(ChatSource source, String message) {
        this(source, message, false);
    }

    private Message(ChatSource source, String message, boolean formatted) {
        this.source = source;
        this.message = message;
        this.formatted = formatted;
    }

    public Message format(Format format) {
        return new Message(source(), format.applyTo(this), true);
    }
}
