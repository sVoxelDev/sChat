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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.core.repository.Entity;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public final class ChatterEntity implements Chatter, Entity<UUID> {

    @Getter
    private final Identity identity;
    private final List<Message> messages = new ArrayList<>();
    private final Set<Channel> channels = Collections.newSetFromMap(new WeakHashMap<>());

    private Channel activeChannel;

    public ChatterEntity(final Identity identity) {
        this.identity = identity;
    }

    @Override
    public @NotNull UUID getKey() {
        return getId();
    }

    public void sendMessage(final @NonNull Message message) {
        this.messages.add(message);
    }

    public @NotNull @Unmodifiable List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public @NotNull Optional<Channel> getActiveChannel() {
        return Optional.ofNullable(activeChannel);
    }

    public void setActiveChannel(Channel channel) {
        addChannel(channel);
        this.activeChannel = channel;
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
        if (isActiveChannel(channel))
            clearActiveChannel();
    }

    public void clearActiveChannel() {
        this.activeChannel = null;
    }

    public boolean isActiveChannel(@Nullable Channel channel) {
        return getActiveChannel().map(c -> c.equals(channel)).orElse(false);
    }

    public @NotNull @Unmodifiable List<Channel> getChannels() {
        return List.copyOf(channels);
    }
}
