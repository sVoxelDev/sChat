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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@Getter
@Setter
@EqualsAndHashCode(of = {"identity"})
final class ChatterImpl implements Chatter {

    static ChatterImpl.Builder builder(Identity identity) {
        return new Builder(identity);
    }

    private final Identity identity;
    private final MessageHandler messageHandler;
    private final PermissionHandler permissionHandler;

    private final Set<Channel> channels = new HashSet<>();
    private final Set<Message> messages = new HashSet<>();

    private @Nullable Channel activeChannel;

    private ChatterImpl(Builder builder) {
        this.identity = builder.identity();
        this.messageHandler = builder.messageHandler();
        this.permissionHandler = builder.permissionHandler();
    }

    @Override
    public @NotNull UUID getKey() {
        return getUniqueId();
    }

    @Override
    public void setActiveChannel(@Nullable Channel activeChannel) {
        if (activeChannel != null)
            join(activeChannel);
        this.activeChannel = activeChannel;
    }

    @Override
    public @NotNull Optional<Channel> getActiveChannel() {
        return Optional.ofNullable(activeChannel);
    }

    @Override
    public boolean isActiveChannel(@Nullable Channel channel) {
        return activeChannel != null && activeChannel.equals(channel);
    }

    @Override
    public @NotNull @Unmodifiable List<Channel> getChannels() {
        return List.copyOf(channels);
    }

    @Override
    public void join(@NonNull Channel channel) {
        channel.addTarget(this);
        this.channels.add(channel);
    }

    @Override
    public boolean isJoined(@Nullable Channel channel) {
        if (channel == null) return false;
        return channels.contains(channel);
    }

    @Override
    public void leave(Channel channel) {
        channel.removeTarget(this);
        this.channels.remove(channel);
    }

    public boolean hasPermission(String permission) {
        return permissionHandler.hasPermission(permission);
    }

    @Override
    public @NotNull @Unmodifiable Set<Message> getMessages() {
        return Collections.unmodifiableSet(messages);
    }

    @Override
    public void sendMessage(@NonNull Message message) {
        messages.add(message);
        messageHandler.handleMessage(message);
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    static final class Builder implements Chatter.Builder {

        private final Identity identity;
        private MessageHandler messageHandler = message -> {};
        private PermissionHandler permissionHandler = permission -> false;

        private Builder(Identity identity) {
            this.identity = identity;
        }

        @Override
        public Chatter create() {
            return new ChatterImpl(this);
        }
    }
}
