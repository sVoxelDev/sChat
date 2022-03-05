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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.commands.SendMessageResult;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.events.channel.ChannelSettingChangedEvent;
import net.silthus.schat.events.channel.ChannelSettingsChanged;
import net.silthus.schat.events.channel.ChannelTargetAdded;
import net.silthus.schat.events.channel.ChannelTargetRemoved;
import net.silthus.schat.events.message.SendChannelMessageEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Messages;
import net.silthus.schat.message.Targets;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.policies.JoinChannelPolicy;
import net.silthus.schat.policies.LeaveChannelPolicy;
import net.silthus.schat.policies.Policy;
import net.silthus.schat.policies.SendChannelMessagePolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.ChannelSettings.PRIORITY;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;
import static net.silthus.schat.commands.SendMessageResult.failure;
import static net.silthus.schat.policies.JoinChannelPolicy.JOIN_CHANNEL_POLICY;
import static net.silthus.schat.policies.LeaveChannelPolicy.LEAVE_CHANNEL_POLICY;
import static net.silthus.schat.policies.SendChannelMessagePolicy.SEND_CHANNEL_MESSAGE_POLICY;

@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"key"})
@ToString(of = {"key", "settings", "targets"})
final class ChannelImpl implements Channel {

    private static final Function<ChannelImpl.Builder, ChannelImpl.Builder> DEFAULTS = builder -> builder
        .policy(JoinChannelPolicy.class, JOIN_CHANNEL_POLICY)
        .policy(SendChannelMessagePolicy.class, SEND_CHANNEL_MESSAGE_POLICY)
        .policy(LeaveChannelPolicy.class, LEAVE_CHANNEL_POLICY);

    @Setter
    private static Function<ChannelImpl.Builder, ChannelImpl.Builder> prototype = builder -> builder;

    static ChannelImpl.Builder builder(String key) {
        return prototype.apply(DEFAULTS.apply(new Builder(key)));
    }

    private static final String VALID_KEY_PATTERN = "^[a-zA-Z0-9_-]+$";

    private final String key;
    private final transient Messages messages = new Messages();
    private final transient EventBus eventBus;
    private final transient Map<Class<? extends Policy>, Policy> policies;
    private @NonNull Targets targets;
    private @NonNull Settings settings;

    private ChannelImpl(Builder builder) {
        this.key = builder.key;
        this.settings = builder.settings
            .withStatic(KEY, key)
            .withStatic(Channel.DISPLAY_NAME, builder.name)
            .create();
        this.targets = builder.targets;
        this.eventBus = builder.eventBus;
        this.policies = Map.copyOf(builder.policies);
    }

    @Override
    public @NotNull ChannelImpl settings(@NonNull Settings settings) {
        final Settings oldSettings = this.settings();
        this.settings = settings.toBuilder()
            .withStatic(KEY, key())
            .withStatic(DISPLAY_NAME, displayName())
            .create();
        eventBus.post(new ChannelSettingsChanged(this, oldSettings, this.settings));
        return this;
    }

    @Override
    public @NotNull <V> Channel set(@NonNull Setting<V> setting, @Nullable V value) {
        final V oldValue = get(setting);
        Channel.super.set(setting, value);
        eventBus.post(new ChannelSettingChangedEvent<>(this, setting, oldValue, value));
        return this;
    }

    public @NotNull @Unmodifiable Messages messages() {
        return Messages.unmodifiable(messages);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P extends Policy> @NotNull Optional<P> policy(@NonNull Class<P> policy) {
        return Optional.ofNullable((P) policies.get(policy));
    }

    @Override
    public @NotNull @Unmodifiable Targets targets() {
        return Targets.unmodifiable(targets);
    }

    @Override
    public @NotNull Channel targets(@NonNull Targets targets) {
        this.targets = Targets.copyOf(targets);
        return this;
    }

    @Override
    public void addTarget(@NonNull MessageTarget target) {
        if (targets.add(target))
            eventBus.post(new ChannelTargetAdded(this, target));
    }

    @Override
    public void removeTarget(@NonNull MessageTarget target) {
        if (isNot(PRIVATE))
            if (targets.remove(target))
                eventBus.post(new ChannelTargetRemoved(this, target));
    }

    @Override
    public SendMessageResult sendMessage(@NonNull Message message) {
        if (messages.add(message))
            return processMessage(message);
        else
            return failure(message);
    }

    private SendMessageResult processMessage(Message message) {
        updateTargets();
        SendChannelMessageEvent event = eventBus.post(new SendChannelMessageEvent(this, message, policy(SendChannelMessagePolicy.class).orElse(SEND_CHANNEL_MESSAGE_POLICY)));
        if (event.isNotCancelled() && event.policy().test(this, message))
            return event.targets().sendMessage(event.message());
        else
            return failure(message);
    }

    @Override
    public int compareTo(@NotNull Channel o) {
        return Comparator.<Channel, Integer>
                comparing(c -> c.get(PRIORITY))
            .thenComparing(c -> c.get(PRIVATE))
            .thenComparing(Channel::key)
            .compare(this, o);
    }

    @Override
    public void close() {
        for (MessageTarget target : targets())
            if (target instanceof Chatter chatter)
                chatter.leave(this);
        targets.clear();
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    static final class Builder implements Channel.Builder {

        private final String key;

        private Component name;
        private Settings.Builder settings = Settings.settingsBuilder();
        private EventBus eventBus = EventBus.empty();
        private Targets targets = new Targets();
        private final Map<Class<? extends Policy>, Policy> policies = new HashMap<>();

        Builder(String key) {
            if (isInvalidKey(key))
                throw new InvalidKey();
            this.key = key;
            this.name = text(key);
            this.settings.withStatic(ChannelSettings.JOIN_PERMISSION, "schat.channel." + key + ".join");
        }

        @Override
        public <V> @NotNull Builder set(final @NonNull Setting<V> setting, final @Nullable V value) {
            this.settings.withStatic(setting, value);
            return this;
        }

        @Override
        public @NotNull Builder settings(@NonNull Settings settings) {
            this.settings = settings.toBuilder();
            return this;
        }

        @Override
        public @NotNull Builder targets(@NonNull Targets targets) {
            this.targets = targets;
            return this;
        }

        @Override
        public <P extends Policy> @NotNull Builder policy(@NonNull Class<P> type, @NotNull P policy) {
            policies.put(type, policy);
            return this;
        }

        @Override
        public @NotNull ChannelImpl create() {
            return new ChannelImpl(this);
        }

        private boolean isInvalidKey(String key) {
            return key == null || key.isBlank() || !key.matches(VALID_KEY_PATTERN);
        }
    }

}
