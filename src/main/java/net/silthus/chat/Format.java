package net.silthus.chat;

import net.silthus.chat.formats.MiniMessageFormat;
import net.silthus.chat.formats.SimpleFormat;
import org.bukkit.configuration.ConfigurationSection;

import static net.silthus.chat.Constants.Formatting.DEFAULT_PREFIX;
import static net.silthus.chat.Constants.Formatting.DEFAULT_SUFFIX;

public interface Format {

    static SimpleFormat defaultFormat() {
        return SimpleFormat.builder().build();
    }

    static SimpleFormat fromConfig(ConfigurationSection config) {
        return SimpleFormat.builder()
                .prefix(config.getString("prefix", DEFAULT_PREFIX))
                .suffix(config.getString("suffix", DEFAULT_SUFFIX))
                .chatColor(config.getString("chat_color"))
                .build();
    }

    static MiniMessageFormat fromMiniMessage(String miniMessage) {
        return new MiniMessageFormat(miniMessage);
    }

    String applyTo(Message message);
}
