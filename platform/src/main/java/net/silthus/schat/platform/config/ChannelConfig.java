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

package net.silthus.schat.platform.config;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.settings.Settings;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Getter
@Setter
@ConfigSerializable
public final class ChannelConfig {

    static ChannelConfig createChannelConfig(ConfigurationAdapter config, String path, String key) {
        final ChannelConfig channelConfig = config.get(path, ChannelConfig.class);
        if (channelConfig == null)
            throw new Invalid();

        channelConfig.setKey(key);
        return channelConfig;
    }

    private transient String key;
    private Component name = Component.empty();
    private Settings settings = Settings.createSettings();

    public static final class Invalid extends RuntimeException {
    }
}
