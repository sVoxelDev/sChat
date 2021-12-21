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
import java.util.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.format.Formatted;
import net.silthus.schat.format.Formatter;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

@Getter
public final class Message implements Comparable<Message>, Formatted {

    public static final @NonNull Predicate<Message> NOT_DELETED = message -> !message.isDeleted();

    public static Message message(final String text) {
        return message(null, text);
    }

    public static Message message(Chatter source, String text) {
        return new Message(source, text);
    }

    private final Formatter<Message> formatter = new MessageFormatter();

    private final Instant timestamp = Instant.now();
    private final Chatter source;
    private final String text;
    private boolean deleted = false;

    private Message(Chatter source, String text) {
        this.source = source;
        this.text = text;
    }

    public void delete() {
        this.deleted = true;
    }

    @Override
    public Component formatted() {
        return formatter.format(this);
    }

    @Override
    public int compareTo(@NotNull final Message o) {
        return getTimestamp().compareTo(o.getTimestamp());
    }

    private static class MessageFormatter implements Formatter<Message> {

        public Component format(Message message) {
            return source(message).append(text(message.getText()));
        }

        private Component source(Message message) {
            if (message.getSource() != null)
                return message.getSource().getDisplayName().append(text(": "));
            else
                return empty();
        }
    }
}
