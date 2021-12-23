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
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.handler.Handler;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Messages;
import net.silthus.schat.message.messenger.Messenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface Chatter extends MessageTarget {

    static Builder builder() {
        return new ChatterImpl.ChatterBuilder();
    }

    static Chatter create() {
        return builder().create();
    }

    @NotNull Component getDisplayName();

    void setDisplayName(@NonNull Component displayName);

    @NotNull Optional<Channel> getActiveChannel();

    void setActiveChannel(@NonNull Channel channel);

    void clearActiveChannel();

    boolean isActiveChannel(@Nullable Channel channel);

    @NotNull @Unmodifiable List<Channel> getChannels();

    void join(@NonNull Channel channel);

    void addChannel(@NonNull Channel channel);

    @NotNull @Unmodifiable Messages getMessages();

    Message chat(@Nullable String text);

    interface Builder {

        Builder messenger(@NonNull Messenger<Chatter> messenger);

        Builder messengerStrategy(@NonNull Messenger.Strategy<Chatter> strategy);

        Builder joinHandler(@NonNull Handler.Join join);

        Builder chatHandler(@NonNull Handler.Chat chat);

        Chatter create();
    }

    class NoActiveChannel extends RuntimeException {
    }
}
