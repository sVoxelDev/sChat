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
import net.silthus.schat.Target;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class Chatter implements Target {

    @Getter
    private final User user;
    private final List<Message> messages = new ArrayList<>();
    private final Set<Channel> channels = Collections.newSetFromMap(new WeakHashMap<>());

    private Channel activeChannel;

    public Chatter(final User user) {
        this.user = user;
    }

    public UUID getId() {
        return user.id();
    }

    public String getName() {
        return user.name();
    }

    public Component getDisplayName() {
        return user.displayName();
    }

    @Override
    public final void sendMessage(final @NonNull Message message) {
        this.messages.add(message);
    }

    public @NotNull @Unmodifiable List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public @NotNull Optional<Channel> getActiveChannel() {
        return Optional.ofNullable(activeChannel);
    }

    public boolean isActiveChannel(final @NonNull Channel channel) {
        return channel.equals(activeChannel);
    }

    public void setActiveChannel(final @NonNull Channel channel) {
        join(channel);
        this.activeChannel = channel;
    }

    public void clearActiveChannel() {
        this.activeChannel = null;
    }

    public @NotNull @Unmodifiable List<Channel> getChannels() {
        return List.copyOf(channels);
    }

    public void join(@NonNull Channel channel) {
        channels.add(channel);
        channel.addTarget(this);
    }

    public void leave(@NonNull Channel channel) {
        channels.remove(channel);
        channel.removeTarget(this);
        if (isActiveChannel(channel))
            clearActiveChannel();
    }
}
