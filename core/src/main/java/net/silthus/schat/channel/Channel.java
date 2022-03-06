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

import java.io.Serial;
import java.util.Optional;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.commands.JoinChannelCommand;
import net.silthus.schat.commands.LeaveChannelCommand;
import net.silthus.schat.events.message.SendChannelMessageEvent;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Messages;
import net.silthus.schat.message.Targets;
import net.silthus.schat.pointer.Configurable;
import net.silthus.schat.pointer.Configured;
import net.silthus.schat.pointer.Pointer;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.policies.JoinChannelPolicy;
import net.silthus.schat.policies.LeaveChannelPolicy;
import net.silthus.schat.policies.Policy;
import net.silthus.schat.policies.SendChannelMessagePolicy;
import net.silthus.schat.repository.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static net.silthus.schat.commands.JoinChannelCommand.joinChannel;
import static net.silthus.schat.pointer.Setting.setting;

/**
 * The channel is a message target that forwards messages sent to it to all joined targets.
 *
 * <p>The behaviour of a channel is controlled by the {@link ChannelSettings} and the use of channel events.
 * Create your own {@link Setting}s and set them using the {@link Builder#set(Setting, Object)} or at runtime
 * with {@link #set(Setting, Object)}.</p>
 *
 * <br><p>{@link Chatter}s can join channels using the {@link JoinChannelCommand} which checks the {@link JoinChannelPolicy}.
 * The policy can be overwritten per channel using the {@link Builder}.
 * The same is applicable for the {@link LeaveChannelCommand} and {@link LeaveChannelPolicy}.</p>
 *
 * <p>When a channel receives a message, the {@link SendChannelMessageEvent} is fired and tested against the {@link SendChannelMessagePolicy}.</p>
 *
 * <p><br>Every channel that is created must also be registered with the {@link ChannelRepository} for it to be accessible by other features.</p>
 *
 * <p>Use the static {@link #createChannel(String)} and {@link #channel(String)} methods to create a new channel.</p>
 * <pre>{@code
 * Channel channel = Channel.channel("my_channel")
 *      .set(ChannelSettings.GLOBAL, true) // creates the channel as a global channel sending the messages to all servers
 *      .policy(JoinChannelPolicy.class, ALLOW) // overrides the join policy and allows everyone to join the channel
 *      .create();
 * sChat.channelRepository().add(channel); // registers the channel in the repository
 * }</pre>
 *
 * @since 1.0.0
 */
public sealed interface Channel extends Entity<String>, Configurable<Channel>, MessageTarget, Comparable<Channel>, AutoCloseable permits ChannelImpl {

    /**
     * This is a pointer to the {@link #key()} of the channel.
     *
     * @since 1.0.0
     */
    Pointer<String> KEY = Pointer.pointer(String.class, "key");
    /**
     * The display name of the channel as it is shown in messages and the view.
     *
     * <p>Default: {@link Channel#key()}</p>
     *
     * @since 1.0.0
     */
    Setting<Component> DISPLAY_NAME = setting(Component.class, "name", Component.empty());

    /**
     * Creates a new channel using the given key.
     *
     * <p>The key must match the following pattern: {@code ^[a-zA-Z0-9_-]+$}.</p>
     *
     * @param key the key of the channel
     * @return the created channel
     * @since 1.0.0
     */
    static @NotNull Channel createChannel(String key) {
        return channel(key).create();
    }

    /**
     * Creates a new channel builder that can be used to customize the new channel.
     *
     * <p>The key must match the following pattern: {@code ^[a-zA-Z0-9_-]+$}.</p>
     *
     * @param key the key of the channel
     * @return the builder of the channel
     * @since 1.0.0
     */
    static @NotNull Builder channel(String key) {
        return ChannelImpl.builder(key);
    }

    /**
     * The key of the channel.
     *
     * @return the key
     * @since 1.0.0
     */
    @NotNull String key();

    /**
     * The display name of the channel.
     *
     * @return the display name
     * @since 1.0.0
     */
    default @NotNull Component displayName() {
        return get(DISPLAY_NAME);
    }

    /**
     * Replaces all settings of the channel with the given settings.
     *
     * @param settings the new settings
     * @return this channel
     * @since 1.0.0
     */
    @NotNull Channel settings(@NonNull Settings settings);

    /**
     * Gets all messages this channel received.
     *
     * @return a list of received messages
     * @since 1.0.0
     */
    @NotNull @Unmodifiable Messages messages();

    /**
     * Gets all targets of this channel.
     *
     * @return a list unmodifiable of targets
     * @since 1.0.0
     */
    @NotNull @Unmodifiable Targets targets();

    /**
     * Replaces all targets of the channel with the given targets.
     *
     * @param targets the targets to set
     * @return this channel
     * @since 1.0.0
     */
    @NotNull Channel targets(@NonNull Targets targets);

    /**
     * Adds a target to this channel without performing any checks.
     *
     * <p>Use the {@link JoinChannelCommand} to join a {@link Chatter} testing all policies.</p>
     *
     * @param target the target to add
     * @since 1.0.0
     */
    void addTarget(@NonNull MessageTarget target);

    /**
     * Removes a target from this channel without performing any checks.
     *
     * <p>Use the {@link LeaveChannelCommand} to join a {@link Chatter} testing all policies.</p>
     *
     * @param target the target to remove
     * @since 1.0.0
     */
    void removeTarget(@NonNull MessageTarget target);

    /**
     * Updates the targets of this channel joining all
     * chatters that are a member of this channel.
     *
     * @since 1.0.0
     */
    default void updateTargets() {
        for (MessageTarget target : targets())
            if (target instanceof Chatter chatter)
                joinChannel(chatter, this);
    }

    /**
     * Tries to find a policy attached to this channel.
     *
     * @param policy the policy to find
     * @param <P>    the policy type
     * @return the policy if the channel has one
     * @since 1.0.0
     */
    <P extends Policy> @NotNull Optional<P> policy(@NonNull Class<P> policy);

    /**
     * Closes this channel and removes all targets from it.
     */
    @Override
    void close();

    /**
     * The builder of a {@link Channel}.
     *
     * @since 1.0.0
     */
    interface Builder extends Configured.Builder<Builder> {

        /**
         * Sets the display name of the channel.
         *
         * @param displayName the name
         * @return this builder
         * @since 1.0.0
         */
        @NotNull Builder name(@NonNull Component displayName);

        /**
         * Sets the targets of the channel.
         *
         * @param targets the targets
         * @return this builder
         * @since 1.0.0
         */
        @NotNull Builder targets(@NonNull Targets targets);

        /**
         * Assigns a policy to the channel.
         *
         * @param type the class of the policy type
         * @param policy the policy
         * @param <P> the type of the policy
         * @return this builder
         * @since 1.0.0
         */
        @NotNull <P extends Policy> Builder policy(@NonNull Class<P> type, @NonNull P policy);

        /**
         * Creates a new instance of the channel using this builder.
         *
         * @return the new channel
         * @since 1.0.0
         */
        @NotNull Channel create();
    }

    /**
     * The exception is thrown if a channel with an invalid key is created.
     *
     * <p>A valid key must match the following pattern: {@code ^[a-zA-Z0-9_-]+$}</p>
     *
     * @since 1.0.0
     */
    final class InvalidKey extends RuntimeException {
        @Serial private static final long serialVersionUID = 4355995383300553731L;
    }
}
