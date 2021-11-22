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
import net.silthus.chat.Format;
import net.silthus.chat.Formats;
import org.bukkit.configuration.ConfigurationSection;

import static net.silthus.chat.Constants.Formatting.BROADCAST;
import static net.silthus.chat.Constants.Formatting.BROADCAST_FORMAT;

@Value
@With
@Builder(toBuilder = true)
@Accessors(fluent = true)
public class BroadcastConfig {

    public static BroadcastConfig broadcastConfig(ConfigurationSection config) {
        return broadcastDefaults().withConfig(config).build();
    }

    public static BroadcastConfig broadcastDefaults() {
        return builder().build();
    }

    @Builder.Default
    Format format = Formats.defaultFormat(BROADCAST);
    @Builder.Default
    boolean includePrivateChats = true;

    public BroadcastConfig.BroadcastConfigBuilder withConfig(ConfigurationSection config) {
        return toBuilder()
                .includePrivateChats(config.getBoolean("include_private_chats", includePrivateChats))
                .format(ConfigUtils.getFormatFromConfig(config, format, BROADCAST, BROADCAST_FORMAT));
    }
}
