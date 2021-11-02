package net.silthus.chat.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(fluent = true)
public class PluginConfig {

    public static PluginConfig fromConfig(ConfigurationSection config) {
        return new PluginConfig(config);
    }

    private ConsoleConfig console;
    private Map<String, ChannelConfig> channels = new HashMap<>();

    private PluginConfig(ConfigurationSection config) {
        loadConsoleConfig(config);
        loadChannels(config);
    }

    private void loadConsoleConfig(ConfigurationSection config) {
        if (config.isConfigurationSection("console")) {
            console = new ConsoleConfig(config.getConfigurationSection("console"));
        } else {
            console = new ConsoleConfig(config.createSection("console"));
        }
    }

    private void loadChannels(ConfigurationSection config) {
        ConfigurationSection channels = config.getConfigurationSection("channels");
        if (channels != null) {
            for (String key : channels.getKeys(false)) {
                this.channels.put(key, ChannelConfig.of(channels.getConfigurationSection(key)));
            }
        }
    }
}
