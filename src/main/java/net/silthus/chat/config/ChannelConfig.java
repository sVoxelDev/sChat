package net.silthus.chat.config;

import lombok.Data;
import lombok.extern.java.Log;
import net.silthus.chat.Format;
import org.bukkit.configuration.ConfigurationSection;

@Log
@Data
public class ChannelConfig {

    private String name;
    private Format format;

    public ChannelConfig(ConfigurationSection config) {
        this.name = config.getString("name");
        this.format = Format.of(config);
    }

    public ChannelConfig() {
        this.format = Format.builder().build();
    }
}
