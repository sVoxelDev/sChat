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

import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Messages;
import net.silthus.schat.message.Targets;
import net.silthus.schat.pointer.Configured;
import net.silthus.schat.pointer.Pointer;
import net.silthus.schat.policies.JoinChannelPolicy;
import net.silthus.schat.policies.Policy;
import net.silthus.schat.policies.SendChannelMessagePolicy;
import net.silthus.schat.repository.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static net.silthus.schat.policies.JoinChannelPolicy.JOIN_CHANNEL_POLICY;
import static net.silthus.schat.policies.SendChannelMessagePolicy.SEND_CHANNEL_MESSAGE_POLICY;

public sealed interface Channel extends Entity<String>, Configured.Modifiable<Channel>, MessageTarget permits ChannelImpl {

    Pointer<String> KEY = Pointer.pointer(String.class, "key");

    static @NotNull Channel createChannel(String key) {
        return channel(key).create();
    }

    static @NotNull Builder channel(String key) {
        return ChannelImpl.builder(key);
    }

    @NotNull String key();

    default @NotNull Component displayName() {
        return get(ChannelSettings.DISPLAY_NAME);
    }

    @NotNull @Unmodifiable Messages messages();

    <P extends Policy> Optional<P> policy(Class<P> policy);

    default JoinChannelPolicy joinPolicy() {
        return policy(JoinChannelPolicy.class).orElse(JOIN_CHANNEL_POLICY);
    }

    default SendChannelMessagePolicy sendMessagePolicy() {
        return policy(SendChannelMessagePolicy.class).orElse(SEND_CHANNEL_MESSAGE_POLICY);
    }

    @NotNull @Unmodifiable Targets targets();

    void addTarget(MessageTarget target);

    void removeTarget(MessageTarget target);

    void updateTargets();

    interface Builder extends Configured.Builder<Builder> {

        Builder name(Component displayName);

        Builder targets(Targets targets);

        <P extends Policy> Builder policy(Class<P> type, P policy);

        Channel create();
    }

    final class InvalidKey extends RuntimeException {
    }
}
