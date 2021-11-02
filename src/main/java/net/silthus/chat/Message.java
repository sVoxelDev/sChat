package net.silthus.chat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import net.kyori.adventure.text.Component;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Message {

    public static Message message(String message) {
        return new Message(ChatSource.nil(), message).withFormat(Format.directMessage());
    }

    public static Message message(ChatSource source, String message) {
        return new Message(source, message);
    }

    ChatSource source;
    String message;
    @With
    Format format;
    ChatTarget target;

    private Message(ChatSource source, String message) {
        this(source, message, Format.defaultFormat(), ChatTarget.nil());
    }

    public Message to(ChatTarget target) {
        if (target == null) return this;
        return new Message(getSource(), getMessage(), getFormat(), target);
    }

    public Component formattedMessage() {
        return getFormat().applyTo(this);
    }

    public Message send() {
        return getTarget().sendMessage(this);
    }
}
