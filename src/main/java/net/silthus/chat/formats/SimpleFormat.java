package net.silthus.chat.formats;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.java.Log;
import net.md_5.bungee.api.ChatColor;
import net.silthus.chat.Format;
import net.silthus.chat.Message;

import static net.silthus.chat.Constants.Formatting.*;

@Log
@Value
@Builder(toBuilder = true)
public class SimpleFormat implements Format {

    @With
    String format;

    @With
    @Builder.Default
    String prefix = DEFAULT_PREFIX;

    @With
    @Builder.Default
    String suffix = DEFAULT_SUFFIX;

    @Builder.Default
    ChatColor chatColor = DEFAULT_CHAT_COLOR;

    @Override
    public String applyTo(Message message) {
        return formatMessageSource(message) +
                formatMessage(message);
    }

    public SimpleFormat withChatColor(String color) {
        return toBuilder().chatColor(color).build();
    }

    public SimpleFormat withChatColor(ChatColor color) {
        return toBuilder().chatColor(color).build();
    }

    private String formatMessage(Message message) {
        StringBuilder sb = new StringBuilder();
        if (getChatColor() != null)
            sb.append(getChatColor());
        return sb.append(message.getMessage()).toString();
    }

    private String formatMessageSource(Message message) {
        StringBuilder sb = new StringBuilder();
        if (message.getSource() != null) {
            if (getPrefix() != null)
                sb.append(getPrefix());
            sb.append(message.getSource().getDisplayName());
            if (getSuffix() != null)
                sb.append(getSuffix());
        }
        return sb.toString();
    }

    public static class SimpleFormatBuilder {

        public SimpleFormatBuilder chatColor(ChatColor color) {
            this.chatColor$set = true;
            this.chatColor$value = color;
            return this;
        }

        public SimpleFormatBuilder chatColor(String color) {
            if (color == null) return this;
            try {
                this.chatColor$value = getChatColor(color);
                this.chatColor$set = true;
            } catch (IllegalArgumentException e) {
                log.warning("Invalid color code \"" + color + "\": " + e.getMessage());
                e.printStackTrace();
            }
            return this;
        }

        private ChatColor getChatColor(String color) {
            boolean isLegacyColorChar = color.length() == 2 && color.startsWith("&");
            if (isLegacyColorChar)
                return getLegacyChatColor(color);
            else
                return ChatColor.of(color);
        }

        private ChatColor getLegacyChatColor(String color) {
            ChatColor colorByChar = ChatColor.getByChar(color.charAt(1));
            if (colorByChar == null)
                throw new IllegalArgumentException("Invalid legacy color char '" + color + "'!");
            return colorByChar;
        }
    }
}
