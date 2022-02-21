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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.commands.SendMessageResult;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.events.chatter.ChatterChangedActiveChannelEvent;
import net.silthus.schat.events.chatter.ChatterJoinedChannelEvent;
import net.silthus.schat.events.chatter.ChatterLeftChannelEvent;
import net.silthus.schat.events.chatter.ChatterReceivedMessageEvent;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Messages;
import net.silthus.schat.pointer.Pointers;
import net.silthus.schat.ui.ViewConnector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import static net.silthus.schat.commands.SendMessageResult.failure;
import static net.silthus.schat.commands.SendMessageResult.success;

@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"identity"})
@ToString(of = {"identity"})
non-sealed class ChatterImpl implements Chatter {

    static final Chatter EMPTY = new EmptyChatter();
    @Getter
    @Setter
    static Function<ChatterImpl.Builder, ChatterImpl.Builder> prototype = builder -> builder;

    static Builder builder(Identity identity) {
        return prototype().apply(new Builder(identity));
    }

    private final Identity identity;
    private final transient @NonNull EventBus eventBus;
    private final transient @NonNull ViewConnector viewConnector;
    private final transient @NonNull PermissionHandler permissionHandler;
    private final transient @NonNull Pointers pointers;

    private final Set<Channel> channels = new HashSet<>();
    private final transient Messages messages = new Messages();

    private @Nullable Channel activeChannel;

    protected ChatterImpl(Builder builder) {
        this.identity = builder.identity();
        this.eventBus = builder.eventBus();
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
    public Chatter activeChannel(@Nullable Channel activeChannel) {
        if (isActiveChannel(activeChannel)) return this;
        if (activeChannel != null)
            join(activeChannel);
        Channel oldChannel = this.activeChannel;
        this.activeChannel = activeChannel;
        fireChangedActiveChannelEvent(oldChannel, activeChannel);
        this.updateView();
        return this;
    }

    private void fireChangedActiveChannelEvent(@Nullable Channel oldChannel, @Nullable Channel newChannel) {
        eventBus().post(new ChatterChangedActiveChannelEvent(this, oldChannel, newChannel));
    }

    @Override
    public @NotNull Optional<Channel> activeChannel() {
        return Optional.ofNullable(activeChannel);
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
        if (this.channels.add(channel)) {
            fireJoinedChannelEvent(channel);
            updateView();
        }
    }

    private void fireJoinedChannelEvent(@NotNull Channel channel) {
        eventBus.post(new ChatterJoinedChannelEvent(this, channel));
    }

    @Override
    public boolean isJoined(@Nullable Channel channel) {
        if (channel == null) return false;
        return channels.contains(channel) && channel.targets().contains(this);
    }

    @Override
    public void leave(@NonNull Channel channel) {
        channel.removeTarget(this);
        if (this.channels.remove(channel))
            fireLeftChannelEvent(channel);
        if (channel.equals(activeChannel))
            activeChannel = null;
    }

    private void fireLeftChannelEvent(@NotNull Channel channel) {
        eventBus().post(new ChatterLeftChannelEvent(this, channel));
    }

    public boolean hasPermission(String permission) {
        return permissionHandler.hasPermission(permission);
    }

    @Override
    public @NotNull @Unmodifiable Messages messages() {
        return Messages.unmodifiable(messages);
    }

    @Override
    public SendMessageResult sendMessage(@NonNull Message message) {
        if (messages.add(message)) {
            fireReceivedMessageEvent(message);
            updateView();
        }
        return success(message);
    }

    private void fireReceivedMessageEvent(@NotNull Message message) {
        eventBus().post(new ChatterReceivedMessageEvent(this, message));
    }

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
        private @NonNull EventBus eventBus = EventBus.empty();

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
        public Chatter activeChannel(@Nullable Channel activeChannel) {
            return this;
        }

        @Override
        public void join(@NonNull Channel channel) {

        }

        @Override
        public boolean isJoined(@Nullable Channel channel) {
            return false;
        }

        @Override
        public void leave(@NonNull Channel channel) {

        }

        @Override
        public @NotNull @Unmodifiable Messages messages() {
            return Messages.of();
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
        public SendMessageResult sendMessage(@NonNull Message message) {
            return failure(message);
        }
    }
}
