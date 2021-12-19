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
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.message.source.MessageSource;
import net.silthus.schat.message.target.MessageTarget;
import net.silthus.schat.message.target.Targets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

/**
 * The message encapsulates text sent from a source and adds a timestamp and id.
 *
 * @since next
 */
public interface Message {

    /**
     * Creates a new empty message.
     *
     * @return the empty message
     * @since next
     */
    static @NotNull Message empty() {
        return message().build();
    }

    /**
     * Creates a new message builder.
     *
     * @return the message builder
     * @since next
     */
    static @NotNull Message.Builder message() {
        return new MessageImpl.MessageBuilderImpl();
    }

    /**
     * Creates a message with the given text.
     *
     * <p>The message will use a {@link MessageSource#nil()} as the source.</p>
     *
     * @param text the text of the message
     * @return a new message with the given text
     * @since next
     */
    static @NotNull Message.Builder message(@NonNull Component text, @NonNull MessageTarget @NonNull ... targets) {
        return message().text(text).to(targets);
    }

    /**
     * Creates a message with the given source and text.
     *
     * @param source the source of the message
     * @param text   the text of the message
     * @return a new message with the given source and text
     * @since next
     */
    static @NotNull Message.Builder message(@NonNull MessageSource source, @NonNull Component text, @NonNull MessageTarget @NonNull ... targets) {
        return message().from(source).text(text).to(targets);
    }

    /**
     * The randomly generated unique id of the message.
     *
     * @return id of the message
     * @since next
     */
    @NotNull UUID getId();

    /**
     * The timestamp of the message creation.
     *
     * @return timestamp of the message
     * @since next
     */
    @NotNull Instant getTimestamp();

    /**
     * The source of the message.
     *
     * <p>The source is never null and cannot be set to null.
     * A {@link MessageSource#nil()} source is used if no source was given.</p>
     *
     * @return the source of the message
     * @since next
     */
    @NotNull MessageSource getSource();

    /**
     * The original text of the message.
     *
     * @return the text of the message
     * @since next
     */
    @NotNull Component getText();

    /**
     * The targets of the message.
     *
     * @return the immutable targets of this message
     * @since next
     */
    @NotNull @Unmodifiable Targets getTargets();

    /**
     * Sends and builds the message using the provided messenger.
     *
     * @param messenger the messenger used to deliver the message
     * @since next
     */
    default Message send(final @NonNull Messenger messenger) {
        messenger.sendMessage(this);
        return this;
    }

    /**
     * The builder of a message that is processed by a {@link Messenger} to create the final {@link Message}.
     *
     * @see Message
     * @since next
     */
    interface Builder {

        /**
         * The source of the future message.
         *
         * <p>The source is never null and cannot be set to null.
         * A {@link MessageSource#nil()} source is used if no source was set.</p>
         *
         * @return the source of the message
         * @since next
         */
        @NotNull MessageSource source();

        /**
         * Sets the source of the message.
         *
         * <p>The source must not be null and defaults to {@link MessageSource#nil()} if not set.</p>
         *
         * @param source the source of the message
         * @return this builder
         * @since next
         */
        @NotNull Builder source(@NotNull MessageSource source);

        /**
         * Sets the source of the message.
         *
         * <p>The source must not be null and defaults to {@link MessageSource#nil()} if not set.</p>
         *
         * @param source the source of the message
         * @return this builder
         * @since next
         */
        default @NotNull Builder from(final @NonNull MessageSource source) {
            return source(source);
        }

        /**
         * The text of the message.
         *
         * @return the text of the message
         * @since next
         */
        @NotNull Component text();

        /**
         * Sets the text of the message.
         *
         * <p>The text must not be null and defaults to a {@link Component#empty()} if not set.</p>
         *
         * @param text the text of the message
         * @return this builder
         * @since next
         */
        @NotNull Builder text(@NotNull Component text);

        /**
         * The targets of the message.
         *
         * @return the targets
         * @since next
         */
        @NotNull Targets targets();

        /**
         * Sets the targets of the message.
         *
         * @param targets the targets of the message
         * @return this builder
         * @since next
         */
        @NotNull Builder targets(@NotNull Targets targets);

        /**
         * Adds the given targets to the targets of the message.
         *
         * <p>Uses the default {@link #targets()} container or the one set with {@link #targets(Targets)}.
         * The {@code targets} are overwritten if {@link #targets(Targets)} is called after this method.</p>
         *
         * @param targets the targets to add
         * @return this builder
         * @since next
         */
        default @NotNull Builder to(final @NonNull MessageTarget @NonNull ... targets) {
            this.targets().addAll(List.of(targets));
            return this;
        }

        @NotNull Message build();

        /**
         * Sends and builds the message using the provided messenger.
         *
         * @param messenger the messenger used to deliver the message
         * @since next
         */
        default Message send(final @NonNull Messenger messenger) {
            return build().send(messenger);
        }
    }
}
