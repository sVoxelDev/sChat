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

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"id"})
final class NewMessageImpl implements NewMessage {

    static NewMessageImpl.Draft builder() {
        return new Draft();
    }

    private final UUID id;
    private final Identity target;
    private final Component text;

    private NewMessageImpl(Draft draft) {
        this.id = draft.id;
        this.target = draft.target;
        this.text = draft.text;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    static final class Draft implements NewMessage.Draft {
        private UUID id = UUID.randomUUID();
        private Identity target = Identity.nil();
        private Component text = Component.empty();

        private Draft() {
        }

        @Override
        public @NotNull Draft to(@NonNull Chatter chatter) {
            this.target = chatter.getIdentity();
            return this;
        }

        public @NotNull NewMessage send(@NonNull Messenger messenger) {
            final Draft draft = messenger.process(this);
            final NewMessage message = ((NewMessageImpl.Draft) draft).build();
            messenger.deliver(message);
            return message;
        }

        NewMessage build() {
            return new NewMessageImpl(this);
        }
    }
}
