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

import java.util.Collection;
import lombok.Getter;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.channel.Channels;
import net.silthus.schat.platform.config.ChannelConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static net.silthus.schat.platform.config.ConfigKeys.CHANNELS;

final class ChannelManager implements Channels {

    @Getter
    private final SChatPlugin plugin;
    @Getter
    private final ChannelRepository repository;

    ChannelManager(SChatPlugin plugin, ChannelRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    @Override
    public void load() {
        for (final ChannelConfig config : getPlugin().getConfig().get(CHANNELS).values()) {
            getRepository().add(createChannelFromConfig(config));
        }
    }

    private Channel createChannelFromConfig(ChannelConfig config) {
        return Channel.channel(config.getKey())
            .displayName(config.getName())
            .settings(config.getSettings())
            .create();
    }

    @Override
    public @NotNull @Unmodifiable Collection<Channel> all() {
        return getRepository().all();
    }

    @Override
    public boolean contains(String key) {
        return getRepository().contains(key);
    }

    @Override
    public @NotNull Channel get(@NotNull String id) throws NotFound {
        return getRepository().get(id);
    }

    @Override
    public void add(@NotNull Channel entity) {
        getRepository().add(entity);
    }

    @Override
    public void remove(@NotNull String key) {
        getRepository().remove(key);
    }
}
