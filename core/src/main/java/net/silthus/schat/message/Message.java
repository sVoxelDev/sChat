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
import java.util.function.Predicate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.repository.Entity;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public final class Message implements Comparable<Message>, Entity<UUID> {

    public static final @NonNull Predicate<Message> NOT_DELETED = message -> !message.isDeleted();

    public static Message message(final String text) {
        return message(null, text);
    }

    public static Message message(Chatter source, String text) {
        return new Message(source, text);
    }

    private final UUID id = UUID.randomUUID();
    private final Instant timestamp = Instant.now();
    private final Chatter source;
    private final String text;
    private boolean deleted = false;

    private Message(Chatter source, String text) {
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
