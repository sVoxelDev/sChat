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
import java.util.Objects;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode(of = {"id"})
public final class Message implements Comparable<Message> {

    public static Message emptyMessage() {
        return message().create();
    }

    public static Builder message() {
        return new Builder();
    }

    public static Builder message(String text) {
        return message().text(text);
    }

    public static Message.Builder message(Component text) {
        return message().text(text);
    }

    private final UUID id = UUID.randomUUID();
    private final Instant timestamp = Instant.now();
    private final Identity source;
    private final Component text;
    private final MessageTarget[] targets;

    private Message(Builder builder) {
        this.source = builder.source;
        this.text = builder.text;
        this.targets = builder.targets;
    }

    public void send() {
        for (final MessageTarget target : targets) {
            target.sendMessage(this);
        }
    }

    @Override
    public int compareTo(@NotNull Message o) {
        return getTimestamp().compareTo(o.getTimestamp());
    }

    public static final class Builder {

        private Identity source = Identity.nil();
        private Component text = Component.empty();
        private MessageTarget[] targets = new MessageTarget[0];

        public Builder source(@NonNull Identity source) {
            this.source = source;
            return this;
        }

        public Builder text(@NonNull Component text) {
            this.text = text;
            return this;
        }

        public Builder text(@NonNull String text) {
            return text(Component.text(text));
        }

        public Builder to(@NonNull MessageTarget... targets) {
            checkForNullTargets(targets);
            this.targets = targets;
            return this;
        }

        public Message create() {
            return new Message(this);
        }

        public Message send() {
            final Message message = create();
            message.send();
            return message;
        }

        private void checkForNullTargets(@NonNull MessageTarget[] targets) {
            for (final MessageTarget target : targets) {
                Objects.requireNonNull(target);
            }
        }
    }
}
