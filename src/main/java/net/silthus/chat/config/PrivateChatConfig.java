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

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;

@Value
@With
@Builder(toBuilder = true)
@Accessors(fluent = true)
public class PrivateChatConfig {

    public static PrivateChatConfig privateChat(ConfigurationSection config) {
        return privateChatDefaults().withConfig(config).build();
    }

    public static PrivateChatConfig privateChatDefaults() {
        return builder().build();
    }

    @Builder.Default
    boolean global = true;

    public PrivateChatConfig.PrivateChatConfigBuilder withConfig(ConfigurationSection config) {
        return toBuilder()
                .global(config.getBoolean("global", global));
    }
}
