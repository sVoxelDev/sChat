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
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode(of = {"id"})
public final class NewMessage {
    @NotNull
    public static NewMessage emptyMessage() {
        return message().create();
    }

    public static Builder message() {
        return new Builder();
    }

    public static NewMessage message(Identity identity, Component text) {
        return message().target(identity).text(text).create();
    }

    private final UUID id;
    private final Identity target;
    private final Component text;

    private NewMessage(Builder builder) {
        this.id = builder.id;
        this.target = builder.target;
        this.text = builder.text;
    }

    public static final class Builder {
        private UUID id = UUID.randomUUID();
        private Identity target = Identity.nil();
        private Component text = Component.empty();

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder target(Identity target) {
            this.target = target;
            return this;
        }

        public Builder text(Component text) {
            this.text = text;
            return this;
        }

        public NewMessage create() {
            return new NewMessage(this);
        }
    }
}
