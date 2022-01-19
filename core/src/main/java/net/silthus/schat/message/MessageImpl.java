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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"id"})
final class MessageImpl implements Message {

    static MessageImpl.Draft builder() {
        return new Draft();
    }

    private final UUID id;
    private final Instant timestamp;
    private final Identity source;
    private final Set<Channel> channels;
    private final Set<MessageTarget> targets;
    private final Component text;
    private final Type type;

    private MessageImpl(Draft draft) {
        this.id = draft.id;
        this.timestamp = draft.timestamp;
        this.source = draft.source;
        this.channels = Set.copyOf(draft.channels);
        this.targets = Set.copyOf(draft.targets);
        this.text = draft.text;
        this.type = draft.type;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    static final class Draft implements Message.Draft {
        private UUID id = UUID.randomUUID();
        private Instant timestamp = Instant.now();
        private Identity source = Identity.nil();
        private Set<Channel> channels = new HashSet<>();
        private Set<MessageTarget> targets = new HashSet<>();
        private Component text = Component.empty();
        private Type type = Type.SYSTEM;

        private Draft() {
        }

        public @NotNull Draft text(@Nullable Component text) {
            if (text != null)
                this.text = text;
            return this;
        }

        @Override
        public @NotNull Draft source(@Nullable Identity source) {
            if (source != null)
                this.source = source;
            return this;
        }

        @Override
        public @NotNull Draft to(@NonNull MessageTarget target) {
            this.targets.add(target);
            return this;
        }

        @Override
        public @NotNull Draft to(@NonNull Channel channel) {
            this.channels.add(channel);
            this.targets.addAll(channel.getTargets());
            return this;
        }

        public @NotNull Message send(@NonNull Messenger messenger) {
            final Draft draft = messenger.process(this);
            final Message message = ((MessageImpl.Draft) draft).build();
            messenger.deliver(message);
            return message;
        }

        Message build() {
            return new MessageImpl(this);
        }
    }
}
