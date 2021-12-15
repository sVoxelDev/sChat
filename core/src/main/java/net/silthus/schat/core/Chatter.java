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
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class Chatter extends MessageTarget {

    private final List<Channel> channels = new ArrayList<>();

    private Channel activeChannel;

    public @NotNull Optional<Channel> getActiveChannel() {
        return Optional.ofNullable(activeChannel);
    }

    public void setActiveChannel(final @NonNull Channel channel) {
        this.activeChannel = channel;
        addChannel(channel);
    }

    public void clearActiveChannel() {
        this.activeChannel = null;
    }

    public @NotNull @Unmodifiable List<Channel> getChannels() {
        return Collections.unmodifiableList(channels);
    }

    public void addChannel(final @NonNull Channel channel) {
        this.channels.add(channel);
    }

    public void removeChannel(final @NonNull Channel channel) {
        this.channels.remove(channel);
        if (isActiveChannel(channel))
            clearActiveChannel();
    }

    private boolean isActiveChannel(final @NonNull Channel channel) {
        return channel.equals(activeChannel);
    }
}
