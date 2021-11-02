package net.silthus.chat;

import net.kyori.adventure.text.Component;
import net.silthus.chat.layout.TabbedChatLayout;

import java.util.Collection;

public interface ChatLayout {

    ChatLayout TABBED = new TabbedChatLayout();

    Component render(Chatter chatter, Message... messages);

    default Component render(Chatter chatter, Collection<Message> messages) {
        return render(chatter, messages.toArray(new Message[0]));
    }
}
