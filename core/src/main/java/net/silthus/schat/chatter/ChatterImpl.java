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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.handler.Handler;
import net.silthus.schat.handler.types.DefaultChatHandler;
import net.silthus.schat.handler.types.DefaultJoinChannelHandler;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Messages;
import net.silthus.schat.message.messenger.Messenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@EqualsAndHashCode(of = {"identity"})
final class ChatterImpl implements Chatter {

    private final List<Channel> channels = new ArrayList<>();
    private final Messenger<Chatter> messenger;
    private final Handler.JoinChannel join;
    private final Handler.Chat chat;

    @Getter
    private final Identity identity;
    private Channel activeChannel;

    private ChatterImpl(ChatterBuilder builder) {
        this.identity = builder.identity;
        this.messenger = builder.messenger;
        this.join = builder.join;
        this.chat = builder.chat;
    }

    @Override
    public @NotNull @Unmodifiable Messages getMessages() {
        return messenger.getMessages();
    }

    @Override
    public boolean isActiveChannel(final @Nullable Channel channel) {
        return activeChannel != null && activeChannel.equals(channel);
    }

    @Override
    public void setActiveChannel(final @NonNull Channel channel) {
        join(channel);
        this.activeChannel = channel;
    }

    @Override
    public @NotNull Optional<Channel> getActiveChannel() {
        return Optional.ofNullable(this.activeChannel);
    }

    @Override
    public void clearActiveChannel() {
        this.activeChannel = null;
    }

    @Override
    public void join(final @NonNull Channel channel) {
        join.joinChannel(this, channel);
    }

    @Override
    public void addChannel(final @NonNull Channel channel) {
        channels.add(channel);
    }

    @Override
    public @NotNull @Unmodifiable List<Channel> getChannels() {
        return Collections.unmodifiableList(channels);
    }

    @Override
    public Message chat(final @Nullable String text) {
        return chat.chat(this, text);
    }

    @Override
    public void sendMessage(final @NonNull Message message) {
        messenger.sendMessage(this, message);
    }

    static class ChatterBuilder implements Builder {

        private final Identity identity;
        private Messenger<Chatter> messenger = Messenger.messenger((message, context) -> {});
        private Handler.JoinChannel join = new DefaultJoinChannelHandler();
        private Handler.Chat chat = new DefaultChatHandler();

        ChatterBuilder(Identity identity) {
            this.identity = identity;
        }

        ChatterBuilder() {
            this(Identity.nil());
        }

        @Override
        public Builder messenger(@NonNull Messenger<Chatter> messenger) {
            this.messenger = messenger;
            return this;
        }

        @Override
        public Builder messengerStrategy(@NonNull Messenger.Strategy<Chatter> strategy) {
            this.messenger = Messenger.messenger(strategy);
            return this;
        }

        @Override
        public Builder joinChannelHandler(@NonNull Handler.JoinChannel join) {
            this.join = join;
            return this;
        }

        @Override
        public Builder chatHandler(@NonNull Handler.Chat chat) {
            this.chat = chat;
            return this;
        }

        @Override
        public Chatter create() {
            return new ChatterImpl(this);
        }
    }
}
