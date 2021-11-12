package net.silthus.chat.renderer;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import net.silthus.chat.Message;
import net.silthus.chat.identities.Chatter;

import java.util.Collection;

@Value
@Accessors(fluent = true)
public class View {

    Chatter chatter;
    Message[] messages;

    public View(@NonNull Chatter chatter, Message... messages) {
        this.chatter = chatter;
        this.messages = messages;
    }

    public View(@NonNull Chatter chatter, @NonNull Collection<Message> messages) {
        this(chatter, messages.toArray(new Message[0]));
    }
}