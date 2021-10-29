package net.silthus.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.silthus.chat.Constants.View.CHANNEL_DIVIDER;
import static net.silthus.chat.Constants.View.FRAME_COLOR;

public class TabbedChatView {

    public Component render(Chatter chatter, Message... messages) {

        return text().append(clearChat())
                .append(renderMessages(messages))
                .append(footer())
                .append(channelTabs(chatter))
                .build();
    }

    public Component footer() {
        return text().build();
//        int leftWidth = MinecraftFont.Font.getWidth("┌");
//        int dashWidth = MinecraftFont.Font.getWidth("─");
//        int rightWidth = MinecraftFont.Font.getWidth("┐");
//        return text("┌─");
    }

    Component channelTabs(Chatter chatter) {
        ArrayList<Component> channels = new ArrayList<>();
        for (Channel channel : chatter.getSubscriptions()) {
            text().append();
        }
        return text()
                .append(text(CHANNEL_DIVIDER + " ").color(FRAME_COLOR))
                .append(text(" " + CHANNEL_DIVIDER).color(FRAME_COLOR))
                .build();
    }

    Component[] renderMessages(Message[] messages) {
        Component[] components = new Component[messages.length];
        for (int i = 0; i < messages.length; i++) {
            components[i] = LegacyComponentSerializer.legacySection().deserialize(messages[i].formattedMessage());
        }
        return components;
    }

    Component clearChat() {
        TextComponent.Builder builder = text();
        for (int i = 0; i < 100; i++) {
            builder.append(newline());
        }
        return builder.build();
    }
}
