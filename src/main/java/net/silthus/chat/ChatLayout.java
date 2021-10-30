package net.silthus.chat;

import net.kyori.adventure.text.Component;
import net.silthus.chat.layout.TabbedChatLayout;

public interface ChatLayout {

    ChatLayout TABBED = new TabbedChatLayout();

    Component render(Chatter chatter, Message... messages);
}
