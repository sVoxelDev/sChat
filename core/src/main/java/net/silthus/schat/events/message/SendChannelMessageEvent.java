/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.silthus.schat.events.message;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.events.Cancellable;
import net.silthus.schat.events.SChatEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Targets;
import net.silthus.schat.policies.SendChannelMessagePolicy;

/**
 * The {@code SendChannelMessageEvent} is fired before a {@link Channel} forwards a message to its {@link Channel#targets()}.
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
 * @since 1.0.0-alpha.4
 */
@Getter
@Setter
@Accessors(fluent = true)
public final class SendChannelMessageEvent implements SChatEvent, Cancellable {

    private final Channel channel;
    private Message message;
    private Targets targets;
    private SendChannelMessagePolicy policy;
    @Getter
    private final AtomicBoolean cancellationState = new AtomicBoolean(false);

    /**
     * Creates a new event.
     *
     * <p>The targets of the channel will be copied on creation.</p>
     *
     * @param channel the channel
     * @param message the message
     * @param policy the policy
     * @since 1.0.0-alpha.4
     */
    public SendChannelMessageEvent(final @NonNull Channel channel, final @NonNull Message message, final @NonNull SendChannelMessagePolicy policy) {
        this.channel = channel;
        this.message = message;
        this.targets = Targets.copyOf(channel.targets());
        this.policy = policy;
    }

    /**
     * Gets the channel that will forward the message.
     *
     * @return the forwarding channel and initial target of the message
     * @since 1.0.0-alpha.4
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
     * @since 1.0.0-alpha.4
     */
    public Message message() {
        return message;
    }

    /**
     * Replaces the message that will be forwarded with a new message.
     *
     * @param message the new message
     * @return this event
     * @since 1.0.0-alpha.4
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
     * @since 1.0.0-alpha.4
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
     * @since 1.0.0-alpha.4
     */
    public SendChannelMessageEvent targets(@NonNull final Targets targets) {
        this.targets = targets;
        return this;
    }

    /**
     * Gets the policy that is checked before the message is sent to the channel.
     *
     * @return the policy of this message event
     */
    public SendChannelMessagePolicy policy() {
        return policy;
    }

    /**
     * Overrides the policy of the channel with the given temporary policy.
     *
     * @param policy the new policy for this message event
     * @return this event
     */
    public SendChannelMessageEvent policy(SendChannelMessagePolicy policy) {
        this.policy = policy;
        return this;
    }
}
