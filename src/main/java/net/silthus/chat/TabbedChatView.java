package net.silthus.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.map.MinecraftFont;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

public class TabbedChatView {

    public Component render(ChatTarget target, Message... messages) {

        return text().append(clearChat())
                .append(renderMessages(messages))
                .append(footer())
                .append(channelTabs())
                .build();
    }

    public Component footer() {
        int leftWidth = MinecraftFont.Font.getWidth("┌");
        int dashWidth = MinecraftFont.Font.getWidth("─");
        int rightWidth = MinecraftFont.Font.getWidth("┐");
        return text("┌─");
    }

    private Component channelTabs() {
        return null;
    }

    private Component[] renderMessages(Message[] messages) {
        Component[] components = new Component[messages.length];
        for (int i = 0; i < messages.length; i++) {
            components[i] = LegacyComponentSerializer.legacySection().deserialize(messages[i].formattedMessage());
        }
        return components;
    }

    private Component clearChat() {
        TextComponent.Builder builder = text();
        for (int i = 0; i < 100; i++) {
            builder.append(newline());
        }
        return builder.build();
    }
}
