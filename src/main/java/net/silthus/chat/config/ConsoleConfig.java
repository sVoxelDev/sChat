package net.silthus.chat.config;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;

@Data
@Accessors(fluent = true)
public class ConsoleConfig {

    private String defaultChannel;

    ConsoleConfig(@NonNull ConfigurationSection config) {
        this.defaultChannel = config.getString("default_channel");
    }
}
