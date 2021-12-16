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

package net.silthus.schat.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class ChatterEntity implements net.silthus.schat.Chatter {

    @Getter
    private final User user;
    private final List<Message> messages = new ArrayList<>();
    private final Set<Channel> channels = Collections.newSetFromMap(new WeakHashMap<>());

    private Channel activeChannel;

    public ChatterEntity(final User user) {
        this.user = user;
    }

    @Override
    public UUID getId() {
        return user.id();
    }

    @Override
    public String getName() {
        return user.name();
    }

    @Override
    public Component getDisplayName() {
        return user.displayName();
    }

    @Override
    public final void sendMessage(final @NonNull Message message) {
        this.messages.add(message);
    }

    @Override
    public @NotNull @Unmodifiable List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public @NotNull Optional<net.silthus.schat.Channel> getActiveChannel() {
        return Optional.ofNullable(activeChannel);
    }

    public void setActiveChannel(final @NonNull Channel channel) {
        this.activeChannel = channel;
    }

    public void clearActiveChannel() {
        this.activeChannel = null;
    }

    @Override
    public @NotNull @Unmodifiable List<net.silthus.schat.Channel> getChannels() {
        return List.copyOf(channels);
    }

    public void addChannel(final @NotNull Channel channel) {
        channels.add(channel);
    }

    public void removeChannel(final @NotNull Channel channel) {
        channels.remove(channel);
    }
}
