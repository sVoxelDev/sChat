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

package net.silthus.schat.platform.plugin;

import lombok.Getter;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.channel.Channels;
import net.silthus.schat.platform.config.ChannelConfig;
import net.silthus.schat.platform.config.Config;

import static net.silthus.schat.platform.config.ConfigKeys.CHANNELS;

final class ChannelManager implements Channels {

    @Getter
    private final Config config;
    @Getter
    private final ChannelRepository repository;

    ChannelManager(Config config, ChannelRepository repository) {
        this.config = config;
        this.repository = repository;
    }

    @Override
    public void load() {
        for (final ChannelConfig config : getConfig().get(CHANNELS).values()) {
            getRepository().add(createChannelFromConfig(config));
        }
    }

    private Channel createChannelFromConfig(ChannelConfig config) {
        return Channel.channel(config.getKey())
            .displayName(config.getName())
            .settings(config.getSettings())
            .create();
    }
}
