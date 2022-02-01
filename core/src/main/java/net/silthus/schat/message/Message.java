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
import java.util.Collection;
import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.pointer.Pointer;
import net.silthus.schat.pointer.Pointered;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import static java.util.Comparator.comparing;
import static net.silthus.schat.pointer.Pointer.pointer;

public sealed interface Message extends Comparable<Message>, Pointered permits MessageImpl {

    Pointer<UUID> ID = pointer(UUID.class, "id");
    Pointer<Instant> TIMESTAMP = pointer(Instant.class, "timestamp");
    Pointer<Identity> SOURCE = pointer(Identity.class, "source");
    Pointer<Component> TEXT = pointer(Component.class, "test");
    Pointer<Type> TYPE = pointer(Type.class, "type");

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

    default boolean hasSource() {
        return !source().equals(Identity.nil());
    }

    @NotNull @Unmodifiable Collection<Channel> channels();

    @NotNull @Unmodifiable Targets targets();

    @NotNull Component text();

    @NotNull Type type();

    @NotNull Message send();

    @NotNull Draft copy();

    @Override
    default int compareTo(@NotNull Message o) {
        return comparing(Message::timestamp)
            .compare(this, o);
    }

    sealed interface Draft permits MessageImpl.Draft {

        @NotNull UUID id();

        @NotNull Draft id(@NonNull UUID id);

        @NotNull Instant timestamp();

        @NotNull Draft timestamp(@NonNull Instant timestamp);

        @NotNull Identity source();

        @NotNull Draft source(@Nullable Identity identity);

        default @NotNull Draft source(@NonNull Chatter chatter) {
            return source(chatter.getIdentity());
        }

        @NotNull Draft to(@NonNull MessageTarget target);

        @NotNull @Unmodifiable Collection<MessageTarget> targets();

        @NotNull Draft to(@NonNull Channel channel);

        @NotNull @Unmodifiable Collection<Channel> channels();

        @NotNull Component text();

        @NotNull Draft text(@Nullable Component text);

        @NotNull Type type();

        @NotNull Draft type(@NonNull Type type);

        @NotNull Message send();

        @NotNull Message create();
    }

    enum Type {
        CHAT,
        SYSTEM
    }
}
