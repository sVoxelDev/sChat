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

package net.silthus.schat.chatter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface Chatter extends MessageTarget {

    UUID getId();

    String getName();

    Component getDisplayName();

    @NotNull @Unmodifiable List<Message> getMessages();

    @NotNull Optional<Channel> getActiveChannel();

    default boolean isActiveChannel(@Nullable Channel channel) {
        return getActiveChannel().map(c -> c.equals(channel)).orElse(false);
    }

    @NotNull @Unmodifiable List<Channel> getChannels();

    void setActiveChannel(Channel channel);

    void join(Channel channel);

    void leave(Channel channel);
}
