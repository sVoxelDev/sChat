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

package net.silthus.schat.message;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import static java.util.Comparator.comparing;

public interface Message extends Comparable<Message> {

    static @NotNull Draft message() {
        return MessageImpl.builder();
    }

    static @NotNull Draft message(@NonNull String text) {
        return message(Component.text(text));
    }

    static @NotNull Draft message(@NonNull Component text) {
        return message().text(text);
    }

    @NotNull UUID id();

    @NotNull Instant timestamp();

    @NotNull Identity source();

    @NotNull @Unmodifiable Set<Channel> channels();

    @NotNull @Unmodifiable Set<MessageTarget> targets();

    @NotNull Component text();

    @NotNull Type type();

    default boolean hasSource() {
        return !source().equals(Identity.nil());
    }

    @Override
    default int compareTo(@NotNull Message o) {
        return comparing(Message::timestamp)
            .compare(this, o);
    }

    interface Draft extends Message {

        @NotNull Draft id(@NonNull UUID id);

        @NotNull Draft timestamp(@NonNull Instant timestamp);

        @NotNull Draft source(@Nullable Identity identity);

        @NotNull Draft to(@NonNull MessageTarget target);

        @NotNull Draft to(@NonNull Channel channel);

        @NotNull Draft text(@Nullable Component text);

        @NotNull Draft type(@NonNull Type type);

        @NotNull Message send(@NonNull Messenger messenger);
    }

    enum Type {
        CHAT,
        SYSTEM
    }
}
