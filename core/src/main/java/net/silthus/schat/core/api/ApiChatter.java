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

package net.silthus.schat.core.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.core.chatter.ChatterEntity;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class ApiChatter implements Chatter {

    private final ChatterEntity handle;

    public ApiChatter(ChatterEntity handle) {
        this.handle = handle;
    }

    @Override
    public UUID getId() {
        return handle.getId();
    }

    @Override
    public String getName() {
        return handle.getName();
    }

    @Override
    public Component getDisplayName() {
        return handle.getDisplayName();
    }

    @Override
    public @NotNull @Unmodifiable List<Message> getMessages() {
        return handle.getMessages();
    }

    @Override
    public @NotNull Optional<Channel> getActiveChannel() {
        return handle.getActiveChannel();
    }

    @Override
    public @NotNull @Unmodifiable List<Channel> getChannels() {
        return handle.getChannels();
    }

    @Override
    public void setActiveChannel(Channel channel) {
        join(channel);
        handle.setActiveChannel(channel);
    }

    @Override
    public void join(Channel channel) {
        handle.addChannel(channel);
        channel.addTarget(this);
    }

    @Override
    public void leave(Channel channel) {
        handle.removeChannel(channel);
        channel.removeTarget(this);
        if (handle.isActiveChannel(channel))
            handle.clearActiveChannel();
    }

    @Override
    public void sendMessage(@NonNull Message message) {
        handle.sendMessage(message);
    }
}
