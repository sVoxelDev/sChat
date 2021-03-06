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
package net.silthus.schat.channel;

import java.util.function.Consumer;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.silthus.schat.policies.JoinChannelPolicy;

import static net.silthus.schat.channel.ChannelSettings.GLOBAL;
import static net.silthus.schat.channel.ChannelSettings.HIDDEN;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;
import static net.silthus.schat.channel.ChannelSettings.PROTECTED;
import static net.silthus.schat.policies.JoinChannelPolicy.CAN_JOIN_PRIVATE_CHANNEL;

/**
 * Prototype class for configuring the settings of a private channel.
 *
 * <p>The here configured template will be used for every private channel
 * that is created using the {@link net.silthus.schat.commands.CreatePrivateChannelCommand}.</p>
 *
 * <p>The {@link #configure(Consumer)} method is non destructive adding to the builder.</p>
 *
 * @since 1.0.0
 */
@Accessors(fluent = true)
public final class PrivateChannel {

    private static final Consumer<Channel.Builder> DEFAULTS = builder -> builder
        .set(GLOBAL, true)
        .set(PRIVATE, true)
        .set(HIDDEN, true)
        .set(PROTECTED, true)
        .policy(JoinChannelPolicy.class, CAN_JOIN_PRIVATE_CHANNEL);

    @Getter
    private static Consumer<Channel.Builder> prototype = DEFAULTS;

    /**
     * Configures the private channel prototype template.
     *
     * <p>The previous builder will not be replaced, but same
     * settings are replaced with the values from this call.</p>
     *
     * @param channel the private channel builder
     * @since 1.0.0
     */
    public static void configure(Consumer<Channel.Builder> channel) {
        prototype = prototype.andThen(channel);
    }

    private PrivateChannel() {
    }
}
