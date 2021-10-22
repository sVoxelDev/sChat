package net.silthus.chat;

import lombok.Value;
import lombok.With;
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
    @With
    Format format;

    public Message(ChatSource source, String message) {
        this(source, message, Format.defaultFormat());
    }

    private Message(ChatSource source, String message, Format format) {
        this.source = source;
        this.message = message;
        this.format = format;
    }

    public String formattedMessage() {
        return format().applyTo(this);
    }
}
