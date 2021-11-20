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
import lombok.extern.java.Log;
import net.silthus.chat.Format;
import net.silthus.chat.Formats;
import net.silthus.chat.Scope;
import net.silthus.chat.Scopes;
import net.silthus.chat.conversations.Channel;
import org.bukkit.configuration.ConfigurationSection;

import static net.silthus.chat.Constants.Scopes.SERVER;

@Log
@Value
@With
@Builder(toBuilder = true)
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelConfig {

    public static ChannelConfig channelConfig(ConfigurationSection config) {
        return channelDefaults().withConfig(config).build();
    }

    public static ChannelConfig channelDefaults() {
        return ChannelConfig.builder().build();
    }

    String name;
    @Builder.Default
    boolean protect = false;
    @Builder.Default
    boolean sendToConsole = true;
    @Builder.Default
    boolean autoJoin = false;
    @Builder.Default
    boolean canLeave = true;
    @Builder.Default
    Format format = Formats.channelFormat();
    @Builder.Default
    transient Scope scope = Scopes.server();
    @Builder.Default
    FooterConfig footer = FooterConfig.builder().build();

    ChannelConfig.ChannelConfigBuilder withConfig(ConfigurationSection config) {
        if (config == null) return toBuilder();
        return toBuilder()
                .name(config.getString("name", name))
                .protect(config.getBoolean("protect", protect))
                .canLeave(!config.getBoolean("force", !canLeave))
                .sendToConsole(config.getBoolean("console", sendToConsole))
                .autoJoin(config.getBoolean("auto_join", autoJoin))
                .scope(Scopes.scope(config.getString("scope", SERVER), config))
                .format(ConfigUtils.getFormatFromConfig(config, format))
                .footer(FooterConfig.footerConfig(config.getConfigurationSection("footer")));
    }

    public Channel toChannel(String identifier) {
        return Channel.channel(identifier, this);
    }
}
