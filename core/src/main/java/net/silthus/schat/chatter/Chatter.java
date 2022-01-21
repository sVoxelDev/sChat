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

package net.silthus.schat.chatter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.repository.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public sealed interface Chatter extends Entity<UUID>, MessageTarget, Identified permits ChatterImpl, ChatterImpl.EmptyChatter {

    /**
     * Gets a chatter with no functionality and the {@link Identity#nil()}.
     *
     * @return an empty chatter
     */
    static Chatter empty() {
        return ChatterImpl.EMPTY;
    }

    static Chatter createChatter(@NonNull Identity identity) {
        return chatter(identity).create();
    }

    static Builder chatter(@NonNull Identity identity) {
        return ChatterImpl.builder(identity);
    }

    @Override
    default @NotNull UUID getKey() {
        return getUniqueId();
    }

    default @NotNull UUID getUniqueId() {
        return getIdentity().getUniqueId();
    }

    default @NotNull String getName() {
        return getIdentity().getName();
    }

    default @NotNull Component getDisplayName() {
        return getIdentity().getDisplayName();
    }

    @NotNull @Unmodifiable List<Channel> getChannels();

    @NotNull Optional<Channel> getActiveChannel();

    boolean isActiveChannel(@Nullable Channel channel);

    void setActiveChannel(@Nullable Channel activeChannel);

    void join(@NonNull Channel channel);

    boolean isJoined(@Nullable Channel channel);

    void leave(Channel channel);

    @NotNull @Unmodifiable Set<Message> getMessages();

    boolean hasPermission(String permission);

    interface Builder {
        Builder messageHandler(MessageHandler messageHandler);

        Builder permissionHandler(PermissionHandler permissionHandler);

        Chatter create();
    }

    interface MessageHandler {
        void handleMessage(Message message, Context context);

        record Context(Chatter chatter) {
        }
    }

    interface PermissionHandler {
        boolean hasPermission(String permission);
    }
}
