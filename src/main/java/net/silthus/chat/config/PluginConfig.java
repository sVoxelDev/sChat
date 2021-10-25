package net.silthus.chat.config;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

@Data
public class PluginConfig {

    private final ConfigurationSection channels;

    public PluginConfig(ConfigurationSection config) {
        ConfigurationSection channels = config.getConfigurationSection("channels");
        this.channels = channels != null ? channels : config.createSection("channels");
    }
}
