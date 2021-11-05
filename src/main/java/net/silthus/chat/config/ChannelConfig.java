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

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Constants;
import net.silthus.chat.Format;
import net.silthus.chat.targets.Channel;
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
