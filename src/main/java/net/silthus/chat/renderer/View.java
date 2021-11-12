package net.silthus.chat.renderer;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import net.silthus.chat.Conversation;
import net.silthus.chat.Message;
import net.silthus.chat.identities.Chatter;

import java.util.Collection;
import java.util.List;

@Value
@Accessors(fluent = true)
public class View {

    Chatter chatter;
    List<Message> messages;
    List<Conversation> conversations;
    Conversation activeConversation;

    public View(@NonNull Chatter chatter, Message... messages) {
        this(chatter, List.of(messages));
    }

    public View(@NonNull Chatter chatter, @NonNull Collection<Message> messages) {
        this.chatter = chatter;
        this.messages = messages.stream().sorted().distinct().toList();
        this.conversations = chatter.getConversations().stream().sorted().toList();
        this.activeConversation = chatter.getActiveConversation();
    }
}