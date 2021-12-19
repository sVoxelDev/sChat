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

package net.silthus.schat.message.source;

import lombok.NonNull;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.target.MessageTarget;
import org.jetbrains.annotations.NotNull;

/**
 * A source identifies the origin of a message and can be used
 * to track the sender or replace placeholders in sent messages
 * using {@link Pointers} to the {@link Identity} behind the source.
 *
 * <p>Every {@link Message} has a {@link #nil()} {@code MessageSource} by default.</p>
 *
 * @since next
 */
public interface MessageSource extends Identified {

    /**
     * Gets a nil message source that has an empty name and display name.
     *
     * <p>All nil message source share the same {@link #getId()} during the lifetime of the application.</p>
     *
     * @return the nil message source
     * @see Identity#nil()
     * @since next
     */
    static @NotNull MessageSource nil() {
        return MessageSources.NIL;
    }

    /**
     * Creates a new message source for the given identity.
     *
     * @param identity the identity of the source
     * @return the message source
     * @since next
     */
    static @NotNull MessageSource messageSource(@NonNull Identity identity) {
        return new MessageSourceImpl(identity);
    }

    /**
     * Creates a new message with this source and the given text.
     *
     * @param text the text
     * @return the message
     * @see Message#message(MessageSource, Component, MessageTarget...)
     * @since next
     */
    default @NotNull Message.Builder message(@NonNull Component text) {
        return Message.message(this, text);
    }
}
