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

package net.silthus.schat.event.types;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.event.Cancellable;
import net.silthus.schat.event.SChatEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Targets;

/**
 * The {@code SendChannelMessageEvent} is fired before a {@link Channel} forwards a message to its {@link Channel#getTargets()}.
 *
 * <p>The {@link SendMessageEvent} will be called before this event and the message will already be sealed.</p>
 *
 * <p>You can however {@link Message#copy()} the message and set a new one that will be forwarded
 * or set new {@link #targets(Targets)} that the message will be forwarded to.</p>
 *
 * <p>The targets in this event are a copy of the channel target list and not backed by the channel.
 * Any modification of those targets are not reflected in the actual targets of the channel.
 * They are only scoped to the forwarded message of this event.</p>
 *
 * @since next
 */
@Accessors(fluent = true)
public final class SendChannelMessageEvent implements SChatEvent, Cancellable {

    private final Channel channel;
    private Message message;
    private Targets targets;
    @Getter
    private final AtomicBoolean cancellationState = new AtomicBoolean(false);

    /**
     * Creates a new event.
     *
     * <p>The targets of the channel will be copied on creation.</p>
     *
     * @param channel the channel
     * @param message the message
     * @since next
     */
    public SendChannelMessageEvent(final @NonNull Channel channel, final @NonNull Message message) {
        this.channel = channel;
        this.message = message;
        this.targets = Targets.copyOf(channel.getTargets());
    }

    /**
     * Gets the channel that will forward the message.
     *
     * @return the forwarding channel and initial target of the message
     * @since next
     */
    public Channel channel() {
        return channel;
    }

    /**
     * Gets the message that was sent to the channel.
     *
     * <p>{@link Message#copy()} the message to modify it and then set it with {@link #message(Message)}.</p>
     *
     * @return the original message sent to the channel
     * @since next
     */
    public Message message() {
        return message;
    }

    /**
     * Replaces the message that will be forwarded with a new message.
     *
     * @param message the new message
     * @return this event
     * @since next
     */
    public SendChannelMessageEvent message(@NonNull final Message message) {
        this.message = message;
        return this;
    }

    /**
     * Gets the targets the message will be forwarded to after this event is done.
     *
     * <p>Use the {@link Targets#filter(Predicate)} method to filter the targets and then set them with {@link #targets(Targets)}.</p>
     *
     * @return the targets of the channel
     * @since next
     */
    public Targets targets() {
        return targets;
    }

    /**
     * Sets the new targets of the message.
     *
     * <p>Changing these targets will not reflect the actual channel targets.</p>
     *
     * @param targets the new targets of the message
     * @return this event
     * @since next
     */
    public SendChannelMessageEvent targets(@NonNull final Targets targets) {
        this.targets = targets;
        return this;
    }
}
