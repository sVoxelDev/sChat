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
import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.commands.JoinChannelCommand;
import net.silthus.schat.commands.LeaveChannelCommand;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.events.channel.ChatterJoinedChannelEvent;
import net.silthus.schat.events.channel.ChatterLeftChannelEvent;
import net.silthus.schat.events.chatter.ChatterChangedActiveChannelEvent;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageSource;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Messages;
import net.silthus.schat.pointer.Pointer;
import net.silthus.schat.policies.JoinChannelPolicy;
import net.silthus.schat.policies.LeaveChannelPolicy;
import net.silthus.schat.repository.Entity;
import net.silthus.schat.util.Permissable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

/**
 * The chatter is a message target that can be used to send and receive messages.
 *
 * <p>Chatters are bound to unique entities (e.g. players or the console) and can join {@link Channel}s.
 * The chatter instances are cached inside the {@link ChatterRepository} and should be retrieved by the {@link ChatterRepository#get(Object)} method.</p>
 *
 * @since 1.0.0
 */
public sealed interface Chatter extends Entity<UUID>, MessageTarget, Identified, MessageSource, Permissable permits ChatterImpl, ChatterImpl.EmptyChatter {

    Pointer<Channel> ACTIVE_CHANNEL = Pointer.pointer(Channel.class, "active_channel");

    /**
     * Gets a chatter with no functionality and the {@link Identity#nil()}.
     *
     * @return an empty chatter
     * @since 1.0.0
     */
    static Chatter empty() {
        return ChatterImpl.EMPTY;
    }

    /**
     * Creates a new chatter for the given identity.
     *
     * @param identity the identity of the chatter
     * @return the created chatter
     * @since 1.0.0
     */
    static Chatter chatter(@NonNull Identity identity) {
        return chatterBuilder(identity).create();
    }

    /**
     * Creates a new chatter builder that allows modifying details of the underlying chatter implementation.
     *
     * @param identity the identity of the chatter
     * @return the builder
     * @since 1.0.0
     */
    static Builder chatterBuilder(@NonNull Identity identity) {
        return ChatterImpl.builder(identity);
    }

    @Override
    default @NotNull UUID key() {
        return uniqueId();
    }

    /**
     * Gets a list of channels attached to this chatter.
     *
     * <p>Just be cause a channel is in this list does not mean the chatter is joined to it.</p>
     *
     * @return a list of channels of this chatter
     * @since 1.0.0
     */
    @NotNull @Unmodifiable List<Channel> channels();

    /**
     * Tries to find a channel with the given key attached to this chatter.
     *
     * @param key the key of the channel
     * @return the channel if the chatter is a member
     * @since 1.0.0
     */
    @NotNull Optional<Channel> channel(String key);

    /**
     * Gets the current active channel of the chatter.
     *
     * @return the active channel
     * @since 1.0.0
     */
    @NotNull Optional<Channel> activeChannel();

    /**
     * Sets the active channel of the chatter.
     *
     * <p>The {@link ChatterChangedActiveChannelEvent} is fired if the active channel changed.</p>
     *
     * <p>The chatter will automatically {@link #join(Channel)} the channel.</p>
     *
     * @param activeChannel the channel to set active
     * @since 1.0.0
     */
    Chatter activeChannel(@Nullable Channel activeChannel);

    /**
     * Checks if the given channel is the active channel of the chatter.
     *
     * <p>This always returns false if the chatter has no active channel.</p>
     *
     * @param channel the channel
     * @return true if the channel is active
     * @since 1.0.0
     */
    default boolean isActiveChannel(@Nullable Channel channel) {
        return activeChannel().map(c -> c.equals(channel)).orElse(false);
    }

    /**
     * Joins the chatter to the given channel ignoring any {@link JoinChannelPolicy}.
     *
     * <p>Use the {@link JoinChannelCommand} to check if the player is allowed to join the channel.</p>
     *
     * <p>The {@link ChatterJoinedChannelEvent} is fired after the chatter joined the channel.</p>
     *
     * @param channel the channel to join
     * @see JoinChannelCommand
     * @since 1.0.0
     */
    void join(@NonNull Channel channel);

    /**
     * Checks if the chatter is a member of the given channel.
     *
     * @param channel the channel to check
     * @return true if the chatter is a member of the channel
     * @since 1.0.0
     */
    boolean isJoined(@Nullable Channel channel);

    /**
     * Leaves the given channel ignoring any {@link LeaveChannelPolicy}.
     *
     * <p>Use the {@link LeaveChannelCommand} to check if the player is allowed to leave the channel.</p>
     *
     * <p>The {@link ChatterLeftChannelEvent} is fired after the chatter left the channel.</p>
     *
     * @param channel the channel to leave
     * @see LeaveChannelCommand
     * @since 1.0.0
     */
    void leave(@NonNull Channel channel);

    /**
     * Gets all messages received by this chatter.
     *
     * @return a list of unmodifiable received messages
     * @since 1.0.0
     */
    @NotNull @Unmodifiable Messages messages();

    /**
     * Sends a raw message to the chatter that will not be wrapped into a {@link Message}.
     *
     * <p>The message is directly forwarded to the underlying platform consumer using the {@link MessageHandler}.</p>
     *
     * @param message the message to sent
     * @since 1.0.0
     */
    void sendRawMessage(Component message);

    /**
     * The builder of a {@link Chatter}.
     *
     * @since 1.0.0
     */
    interface Builder {
        /**
         * Sets the event bus to use by this chatter.
         *
         * <p>This should be set for all chatters using the {@link ChatterPrototype}.</p>
         *
         * @param eventBus the event bus
         * @return this builder
         * @since 1.0.0
         */
        @ApiStatus.Internal
        @NotNull Builder eventBus(@NonNull EventBus eventBus);

        /**
         * Sets the message handler used by this chatter.
         *
         * <p>This is the gateway to the consuming client that processes and forwards the actual rendered messages.</p>
         *
         * @param messageHandler the message handler to set
         * @return this builder
         * @since 1.0.0
         */
        @ApiStatus.Internal
        @NotNull Builder messageHandler(@NonNull MessageHandler messageHandler);

        /**
         * Sets the permission handler used by this chatter.
         *
         * <p>This should be done by the {@link ChatterFactory} of the implementing platform.</p>
         *
         * @param permissionHandler the permission handler
         * @return this builder
         * @since 1.0.0
         */
        @ApiStatus.Internal
        @NotNull Builder permissionHandler(@NonNull PermissionHandler permissionHandler);

        /**
         * Creates the chatter.
         *
         * @return the newly created chatter
         * @since 1.0.0
         */
        @NotNull Chatter create();
    }

    /**
     * The permission handler used by a chatter to validate a permission node.
     *
     * @since 1.0.0
     */
    interface PermissionHandler {
        /**
         * Calls the handler to check if the chatter has the given permission.
         *
         * @param permission the permission to check
         * @return true if the chatter has the permission
         * @since 1.0.0
         */
        boolean hasPermission(String permission);
    }

    /**
     * The message handler is used to dispatch raw rendered messages to the chatter.
     *
     * <p>The message is directly forwarded to the receiver on the implementing platform.</p>
     *
     * @since 1.0.0
     */
    interface MessageHandler {

        /**
         * Sends a raw rendered message or view to the chatter.
         *
         * <p>This method should, in the most cases, only be called by the view after it has been rendered.</p>
         *
         * @param message the raw message to sent
         * @since 1.0.0
         */
        void sendRawMessage(Component message);
    }
}
