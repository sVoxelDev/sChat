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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Pointers;
import net.silthus.schat.view.ViewConnector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"identity"})
@ToString(of = {"identity"})
non-sealed class ChatterImpl implements Chatter {

    static final Chatter EMPTY = new EmptyChatter();

    static ChatterImpl.Builder builder(Identity identity) {
        return new Builder(identity);
    }

    private final Identity identity;
    private final transient ViewConnector viewConnector;
    private final transient PermissionHandler permissionHandler;
    private final transient Pointers pointers;

    private final Set<Channel> channels = new HashSet<>();
    private final transient Queue<Message> messages = new LinkedList<>();

    private @Nullable Channel activeChannel;

    protected ChatterImpl(Builder builder) {
        this.identity = builder.identity();
        this.viewConnector = builder.viewConnector().create(this);
        this.permissionHandler = builder.permissionHandler();
        this.pointers = Pointers.pointersBuilder()
            .withForward(Identity.ID, identity(), Identity.ID)
            .withForward(Identity.NAME, identity(), Identity.NAME)
            .withForward(Identity.DISPLAY_NAME, identity(), Identity.DISPLAY_NAME)
            .withDynamic(ACTIVE_CHANNEL, () -> activeChannel().orElse(null))
            .create();
    }

    @Override
    public void activeChannel(@Nullable Channel activeChannel) {
        if (isActiveChannel(activeChannel)) return;
        if (activeChannel != null)
            join(activeChannel);
        this.activeChannel = activeChannel;
        this.updateView();
    }

    @Override
    public @NotNull Optional<Channel> activeChannel() {
        return Optional.ofNullable(activeChannel);
    }

    @Override
    public boolean isActiveChannel(@Nullable Channel channel) {
        return activeChannel != null && activeChannel.equals(channel);
    }

    @Override
    public @NotNull @Unmodifiable List<Channel> channels() {
        return List.copyOf(channels);
    }

    @Override
    public @NotNull Optional<Channel> channel(String key) {
        return channels().stream().filter(channel -> channel.key().equals(key)).findFirst();
    }

    @Override
    public void join(@NonNull Channel channel) {
        channel.addTarget(this);
        if (this.channels.add(channel))
            updateView();
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
    public @NotNull @Unmodifiable Set<Message> messages() {
        return Set.copyOf(messages);
    }

    @Override
    public Optional<Message> lastMessage() {
        return Optional.ofNullable(messages.peek());
    }

    @Override
    public void sendMessage(@NonNull Message message) {
        messages.add(message);
        updateView();
    }

    @Override
    public void updateView() {
        viewConnector.update();
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    static final class Builder implements Chatter.Builder {

        private final Identity identity;
        private @NonNull ViewConnector.Factory viewConnector = chatter -> () -> {};
        private @NonNull PermissionHandler permissionHandler = permission -> false;

        private Builder(Identity identity) {
            this.identity = identity;
        }

        @Override
        public @NotNull Chatter create() {
            return new ChatterImpl(this);
        }
    }

    static final class EmptyChatter implements Chatter {

        @Override
        public @NotNull @Unmodifiable List<Channel> channels() {
            return List.of();
        }

        @Override
        public @NotNull Optional<Channel> channel(String key) {
            return Optional.empty();
        }

        @Override
        public @NotNull Optional<Channel> activeChannel() {
            return Optional.empty();
        }

        @Override
        public boolean isActiveChannel(@Nullable Channel channel) {
            return false;
        }

        @Override
        public void activeChannel(@Nullable Channel activeChannel) {

        }

        @Override
        public void join(@NonNull Channel channel) {

        }

        @Override
        public boolean isJoined(@Nullable Channel channel) {
            return false;
        }

        @Override
        public void leave(Channel channel) {

        }

        @Override
        public @NotNull @Unmodifiable Set<Message> messages() {
            return Set.of();
        }

        @Override
        public Optional<Message> lastMessage() {
            return Optional.empty();
        }

        @Override
        public void updateView() {

        }

        @Override
        public boolean hasPermission(String permission) {
            return false;
        }

        @Override
        public @NotNull Identity identity() {
            return Identity.nil();
        }

        @Override
        public void sendMessage(@NonNull Message message) {

        }
    }
}
