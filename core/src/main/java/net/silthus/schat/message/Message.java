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
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.repository.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public final class Message implements Comparable<Message>, Entity<UUID> {

    public static Builder message() {
        return new Builder();
    }

    public static Message message(final String text) {
        return message(text(text));
    }

    public static Message message(Identity source, String text) {
        return message(source, text(text));
    }

    public static Message message(Component text) {
        return message().text(text).create();
    }

    public static Message message(Identity source, Component text) {
        return message().source(source).text(text).create();
    }

    private final UUID id = UUID.randomUUID();
    private final Instant timestamp = Instant.now();
    private final @NotNull Identity source;
    private final @NotNull Component text;
    private final Type type;
    private boolean deleted = false;

    private Message(@NotNull Identity source, @NotNull Component text, Type type) {
        this.source = source;
        this.text = text;
        this.type = type;
    }

    @Override
    public @NotNull UUID getKey() {
        return getId();
    }

    @Override
    public int compareTo(@NotNull final Message o) {
        return getTimestamp().compareTo(o.getTimestamp());
    }

    public final static class Builder {

        private @NotNull Identity source = Identity.nil();
        private @NotNull Component text = empty();
        private Type type;

        private Builder() {
        }

        public Builder source(@NonNull Identity source) {
            this.source = source;
            return this;
        }

        public Builder text(@NonNull Component text) {
            this.text = text;
            return this;
        }

        public Builder type(@NonNull Type type) {
            this.type = type;
            return this;
        }

        public Message create() {
            if (type == null) {
                if (source == Identity.nil())
                    type = Type.SYSTEM;
                else
                    type = Type.CHAT;
            }
            return new Message(source, text, type);
        }

        public Message send(MessageTarget target) {
            final Message message = create();
            target.sendMessage(message);
            return message;
        }

    }

    public enum Type {
        CHAT,
        SYSTEM
    }
}
