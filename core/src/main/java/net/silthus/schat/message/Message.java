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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public final class Message implements Comparable<Message>, Entity<UUID> {

    public static Message message(final String text) {
        return message(null, text);
    }

    public static Message message(Identity source, String text) {
        return message(source, text(text));
    }

    public static Message message(Component text) {
        return message(null, text);
    }

    public static Message message(Identity source, Component text) {
        return new Message(source, text);
    }

    private final UUID id = UUID.randomUUID();
    private final Instant timestamp = Instant.now();
    private final @Nullable Identity source;
    private final @NotNull Component text;
    private boolean deleted = false;

    private Message(@Nullable Identity source, @NotNull Component text) {
        this.source = source;
        this.text = text;
    }

    @Override
    public @NotNull UUID getKey() {
        return getId();
    }

    @Override
    public int compareTo(@NotNull final Message o) {
        return getTimestamp().compareTo(o.getTimestamp());
    }
}
