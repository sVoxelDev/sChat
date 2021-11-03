package net.silthus.chat.config;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import net.silthus.chat.Channel;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Constants;
import net.silthus.chat.Format;
import org.bukkit.configuration.ConfigurationSection;

@Log
@Data
@Accessors(fluent = true)
public class ChannelConfig {

    public static ChannelConfig of(ConfigurationSection config) {
        return new ChannelConfig(config);
    }

    public static ChannelConfig defaults() {
        return new ChannelConfig();
    }

    private String name;
    private boolean protect = false;
    private boolean sendToConsole = true;
    private boolean autoJoin = true;
    private Format format = Format.channelFormat();

    private ChannelConfig(ConfigurationSection config) {
        this.name = config.getString("name");
        this.protect = config.getBoolean("protect", protect);
        this.sendToConsole = config.getBoolean("console", sendToConsole);
        this.autoJoin = config.getBoolean("auto_join", autoJoin);
        this.format = Format.miniMessage(config.getString("format", Constants.Formatting.DEFAULT_CHANNEL_FORMAT));
    }

    private ChannelConfig() {}

    public Channel toChannel(String identifier) {
        return ChatTarget.channel(identifier, this);
    }
}
