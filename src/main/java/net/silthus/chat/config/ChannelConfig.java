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
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Format;
import net.silthus.chat.Scope;
import net.silthus.chat.Scopes;
import net.silthus.chat.conversations.Channel;
import org.bukkit.configuration.ConfigurationSection;

import static net.silthus.chat.Constants.Scopes.SERVER;

@Log
@Data
@Builder(toBuilder = true)
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelConfig {

    public static ChannelConfig of(@NonNull ConfigurationSection config) {
        return defaults().withConfig(config).build();
    }

    public static ChannelConfig of(ConfigurationSection config, ChannelConfig defaults) {
        return defaults.withConfig(config).build();
    }

    public static ChannelConfig defaults() {
        return ChannelConfig.builder().build();
    }

    private String name;
    @Builder.Default
    private boolean protect = false;
    @Builder.Default
    private boolean sendToConsole = true;
    @Builder.Default
    private boolean autoJoin = false;
    @Builder.Default
    private boolean canLeave = true;
    @Builder.Default
    private Format format = Format.channelFormat();
    @Builder.Default
    private transient Scope scope = Scopes.server();
    @Builder.Default
    private FooterConfig footer = FooterConfig.builder().build();

    ChannelConfig.ChannelConfigBuilder withConfig(ConfigurationSection config) {
        if (config == null) return toBuilder();
        return toBuilder()
                .name(config.getString("name", name))
                .protect(config.getBoolean("protect", protect))
                .canLeave(!config.getBoolean("force", !canLeave))
                .sendToConsole(config.getBoolean("console", sendToConsole))
                .autoJoin(config.getBoolean("auto_join", autoJoin))
                .scope(Scopes.scope(config.getString("scope", SERVER), config))
                .format(config.isSet("format") ? Format.miniMessage(config.getString("format")) : format)
                .footer(FooterConfig.fromConfig(config.getConfigurationSection("footer")));
    }

    public Channel toChannel(String identifier) {
        return ChatTarget.channel(identifier, this);
    }
}
