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
import net.silthus.schat.events.chatter.ChatterChangedActiveChannelEvent;
import net.silthus.schat.events.chatter.ChatterJoinedChannelEvent;
import net.silthus.schat.events.chatter.ChatterLeftChannelEvent;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Messages;
import net.silthus.schat.pointer.Pointer;
import net.silthus.schat.policies.JoinChannelPolicy;
import net.silthus.schat.policies.LeaveChannelPolicy;
import net.silthus.schat.repository.Entity;
import net.silthus.schat.ui.ViewConnector;
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
 * @since next
 */
public sealed interface Chatter extends Entity<UUID>, MessageTarget, Identified, Permissable permits ChatterImpl, ChatterImpl.EmptyChatter {

    Pointer<Channel> ACTIVE_CHANNEL = Pointer.pointer(Channel.class, "active_channel");

    /**
     * Gets a chatter with no functionality and the {@link Identity#nil()}.
     *
     * @return an empty chatter
     * @since next
     */
    static Chatter empty() {
        return ChatterImpl.EMPTY;
    }

    /**
     * Creates a new chatter for the given identity.
     *
     * @param identity the identity of the chatter
     * @return the created chatter
     * @since next
     */
    static Chatter chatter(@NonNull Identity identity) {
        return chatterBuilder(identity).create();
    }

    /**
     * Creates a new chatter builder that allows modifying details of the underlying chatter implementation.
     *
     * @param identity the identity of the chatter
     * @return the builder
     * @since next
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
     * @since next
     */
    @NotNull @Unmodifiable List<Channel> channels();

    /**
     * Tries to find a channel with the given key attached to this chatter.
     *
     * @param key the key of the channel
     * @return the channel if the chatter is a member
     * @since next
     */
    @NotNull Optional<Channel> channel(String key);

    /**
     * Gets the current active channel of the chatter.
     *
     * @return the active channel
     * @since next
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
     * @since next
     */
    Chatter activeChannel(@Nullable Channel activeChannel);

    /**
     * Checks if the given channel is the active channel of the chatter.
     *
     * <p>This always returns false if the chatter has no active channel.</p>
     *
     * @param channel the channel
     * @return true if the channel is active
     * @since next
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
     * @since next
     */
    void join(@NonNull Channel channel);

    /**
     * Checks if the chatter is a member of the given channel.
     *
     * @param channel the channel to check
     * @return true if the chatter is a member of the channel
     * @since next
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
     * @since next
     */
    void leave(@NonNull Channel channel);

    /**
     * Gets all messages received by this chatter.
     *
     * @return a list of unmodifiable received messages
     * @since next
     */
    @NotNull @Unmodifiable Messages messages();

    /**
     * Helper method to send a message using this chatter as the source.
     *
     * @param text the text of the message
     * @return the message draft
     * @since next
     */
    default Message.Draft message(String text) {
        return Message.message(text).source(this);
    }

    /**
     * Helper method to send a message using this chatter as the source.
     *
     * @param text the text of the message
     * @return the message draft
     * @since next
     */
    default Message.Draft message(Component text) {
        return Message.message(text).source(this);
    }

    /**
     * The builder of a {@link Chatter}.
     *
     * @since next
     */
    interface Builder {
        /**
         * Sets the event bus to use by this chatter.
         *
         * <p>This should be set for all chatters using the {@link ChatterPrototype}.</p>
         *
         * @param eventBus the event bus
         * @return this builder
         * @since next
         */
        @ApiStatus.Internal
        @NotNull Builder eventBus(EventBus eventBus);

        @ApiStatus.Internal
        @NotNull Builder viewConnector(@NonNull ViewConnector.Factory viewConnectorFactory);

        /**
         * Sets the permission handler used by this chatter.
         *
         * <p>This should be done by the {@link ChatterFactory} of the implementing platform.</p>
         *
         * @param permissionHandler the permission handler
         * @return this builder
         * @since next
         */
        @ApiStatus.Internal
        @NotNull Builder permissionHandler(@NonNull PermissionHandler permissionHandler);

        /**
         * Creates the chatter.
         *
         * @return the newly created chatter
         * @since next
         */
        @NotNull Chatter create();
    }

    /**
     * The permission handler used by a chatter to validate a permission node.
     *
     * @since next
     */
    interface PermissionHandler {
        /**
         * Calls the handler to check if the chatter has the given permission.
         *
         * @param permission the permission to check
         * @return true if the chatter has the permission
         * @since next
         */
        boolean hasPermission(String permission);
    }
}
