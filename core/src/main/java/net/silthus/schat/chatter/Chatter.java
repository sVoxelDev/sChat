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
import net.silthus.schat.util.Permissable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public sealed interface Chatter extends Entity<UUID>, MessageTarget, Identified, Permissable permits ChatterImpl, ChatterImpl.EmptyChatter {

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
