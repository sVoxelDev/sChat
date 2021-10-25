package net.silthus.chat;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.java.Log;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import static net.silthus.chat.Constants.Formatting.*;

@Log
@Value
@Builder(toBuilder = true)
public class Format {

    public static Format defaultFormat() {
        return builder().build();
    }

    public static Format of(ConfigurationSection config) {
        return Format.builder()
                .prefix(config.getString("prefix", DEFAULT_PREFIX))
                .suffix(config.getString("suffix", DEFAULT_SUFFIX))
                .chatColor(config.getString("chat_color"))
                .build();
    }

    @With
    @Builder.Default
    String prefix = DEFAULT_PREFIX;
    @With
    @Builder.Default
    String suffix = DEFAULT_SUFFIX;

    @Builder.Default
    ChatColor chatColor = DEFAULT_CHAT_COLOR;

    public Format withChatColor(String color) {
        return toBuilder().chatColor(color).build();
    }

    public Format withChatColor(ChatColor color) {
        return toBuilder().chatColor(color).build();
    }

    public String applyTo(Message message) {
        return formatMessageSource(message) +
                formatMessage(message);
    }

    @NotNull
    private String formatMessage(Message message) {
        StringBuilder sb = new StringBuilder();
        if (getChatColor() != null)
            sb.append(getChatColor());
        return sb.append(message.message()).toString();
    }

    private String formatMessageSource(Message message) {
        StringBuilder sb = new StringBuilder();
        if (message.source() != null) {
            if (getPrefix() != null)
                sb.append(getPrefix());
            sb.append(message.source().getDisplayName());
            if (getSuffix() != null)
                sb.append(getSuffix());
        }
        return sb.toString();
    }

    public static class FormatBuilder {

        public FormatBuilder chatColor(ChatColor color) {
            this.chatColor$set = true;
            this.chatColor$value = color;
            return this;
        }

        public FormatBuilder chatColor(String color) {
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
