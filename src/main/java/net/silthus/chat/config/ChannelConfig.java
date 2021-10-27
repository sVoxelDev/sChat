package net.silthus.chat.config;

import lombok.Data;
import lombok.extern.java.Log;
import net.silthus.chat.Format;
import org.bukkit.configuration.ConfigurationSection;

@Log
@Data
public class ChannelConfig {

    public static ChannelConfig of(ConfigurationSection config) {
        return new ChannelConfig(config);
    }

    private String name;
    private Format format = Format.defaultFormat();

    public ChannelConfig() {}

    private ChannelConfig(ConfigurationSection config) {
        this.name = config.getString("name");
        ConfigurationSection formatSection = config.getConfigurationSection("format");
        if (formatSection != null)
            this.format = Format.of(formatSection);
    }
}
