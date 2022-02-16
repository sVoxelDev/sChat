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
import net.silthus.schat.pointer.Pointer;
import net.silthus.schat.repository.Entity;
import net.silthus.schat.util.Permissable;
import net.silthus.schat.view.ViewConnector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public sealed interface Chatter extends Entity<UUID>, MessageTarget, Identified, Permissable permits ChatterImpl, ChatterImpl.EmptyChatter {

    Pointer<Channel> ACTIVE_CHANNEL = Pointer.pointer(Channel.class, "active_channel");

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
    default @NotNull UUID key() {
        return uniqueId();
    }

    @NotNull @Unmodifiable List<Channel> channels();

    @NotNull Optional<Channel> channel(String key);

    @NotNull Optional<Channel> activeChannel();

    void activeChannel(@Nullable Channel activeChannel);

    default boolean isActiveChannel(@Nullable Channel channel) {
        return activeChannel().map(c -> c.equals(channel)).orElse(false);
    }

    void join(@NonNull Channel channel);

    boolean isJoined(@Nullable Channel channel);

    void leave(Channel channel);

    @NotNull @Unmodifiable Set<Message> messages();

    Optional<Message> lastMessage();

    void updateView();

    default Message.Draft message(String text) {
        return Message.message(text).source(this);
    }

    default Message.Draft message(Component text) {
        return Message.message(text).source(this);
    }

    interface Builder {
        @NotNull Builder viewConnector(@NonNull ViewConnector.Factory viewConnectorFactory);

        @NotNull Builder permissionHandler(@NonNull PermissionHandler permissionHandler);

        @NotNull Chatter create();
    }

    interface PermissionHandler {
        boolean hasPermission(String permission);
    }
}
