package net.silthus.chat;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

@Log
@Getter
public class Channel {

    private final String alias;
    @Accessors(fluent = true)
    private final Config config;

    public Channel(String alias) {
        this.alias = alias;
        this.config = new Config();
    }

    public Channel(String alias, ConfigurationSection config) {
        this.alias = alias;
        this.config = new Config(config);
    }

    public String getPermission() {
        return Constants.CHANNEL_PERMISSION + "." + getAlias();
    }

    public String format(ChatMessage message) {
        return config().prefix() + message.player().getDisplayName() + config().suffix() + config().color() + message.message();
    }

    @Data
    @Accessors(fluent = true)
    class Config {

        private String name = alias;
        private String prefix;
        private String suffix = ": ";
        private ChatColor color = ChatColor.WHITE;

        private Config(ConfigurationSection config) {
            this.name = config.getString("name", alias);
            this.prefix = config.getString("prefix");
            this.suffix = config.getString("suffix");
            this.color = ChatColor.of(config.getString("color", color.getName()));
        }

        private Config() {
        }

        public Config color(ChatColor color) {
            this.color = color;
            return this;
        }

        public Config color(String color) {
            try {
                this.color = getChatColor(color);
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
