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
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.message.source.MessageSource;
import net.silthus.schat.message.target.Targets;
import org.jetbrains.annotations.NotNull;

@Getter
final class MessageImpl implements Message {

    private final UUID id = UUID.randomUUID();
    private final Instant timestamp = Instant.now();
    private final MessageSource source;
    private final Component text;
    private final Targets targets;

    private MessageImpl(final MessageSource source, final Component text, final Targets targets) {
        this.source = source;
        this.text = text;
        this.targets = targets;
    }

    @Data
    @Accessors(fluent = true)
    static final class MessageBuilderImpl implements Builder {

        private MessageSource source = MessageSource.nil();
        private Component text = Component.empty();
        private Targets targets = new Targets();

        @Override
        public @NotNull Message build() {
            return new MessageImpl(source, text, Targets.unmodifiable(targets));
        }
    }
}
