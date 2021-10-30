package net.silthus.chat;

import net.kyori.adventure.text.Component;

public interface ChatLayout {

    ChatLayout TABBED = new TabbedChatLayout();

    Component render(Chatter chatter, Message... messages);
}
