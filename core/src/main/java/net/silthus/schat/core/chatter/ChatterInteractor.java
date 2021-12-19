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

package net.silthus.schat.core.chatter;

import java.util.UUID;
import java.util.function.Consumer;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.Chatters;

public final class ChatterInteractor implements Chatters {

    private final ChatterRepository repository;

    public ChatterInteractor(final ChatterRepository repository) {
        this.repository = repository;
    }

    @Override
    public ChatterEntity getPlayer(final UUID playerId) {
        return repository.getPlayer(playerId);
    }

    @Override
    public void setActiveChannel(final Chatter chatter, final Channel channel) {
        joinChannel(chatter, channel);
        ifPresent(chatter, chatterEntity -> chatterEntity.setActiveChannel(channel));
    }

    @Override
    public void joinChannel(final Chatter chatter, final Channel channel) {
        ifPresent(chatter, chatterEntity -> {
            chatterEntity.addChannel(channel);
            channel.addTarget(chatterEntity);
        });
    }

    @Override
    public void leaveChannel(final Chatter chatter, final Channel channel) {
        ifPresent(chatter, chatterEntity -> {
            chatterEntity.removeChannel(channel);
            channel.removeTarget(chatterEntity);
        });
    }

    private void ifPresent(final Chatter chatter, Consumer<ChatterEntity> command) {
        repository.get(chatter.getId()).ifPresent(command);
    }
}
