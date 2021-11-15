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

@Data
@Builder
@NoArgsConstructor
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsoleConfig {

    @Builder.Default
    private Component name = text("Console");
    @Builder.Default
    private String defaultChannel = "global";

    ConsoleConfig(@NonNull ConfigurationSection config) {
        this.name = MiniMessage.miniMessage().deserialize(config.getString("name", "Console"));
        this.defaultChannel = config.getString("default_channel", defaultChannel);
    }
}
