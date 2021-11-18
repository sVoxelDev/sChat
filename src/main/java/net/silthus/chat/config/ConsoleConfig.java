/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.chat.config;

import lombok.*;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;

import static net.kyori.adventure.text.Component.text;

@Value
@With
@Builder(toBuilder = true)
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsoleConfig {

    public static ConsoleConfig consoleConfig(ConfigurationSection config) {
        return consoleDefaults().withConfig(config).build();
    }

    public static ConsoleConfig consoleDefaults() {
        return builder().build();
    }

    @Builder.Default
    Component name = text("Console");
    @Builder.Default
    String defaultChannel = "global";

    public ConsoleConfig.ConsoleConfigBuilder withConfig(ConfigurationSection config) {
        if (config == null) return toBuilder();
        return toBuilder()
                .name(MiniMessage.miniMessage().deserialize(config.getString("name", "Console")))
                .defaultChannel(config.getString("default_channel", defaultChannel));
    }
}
