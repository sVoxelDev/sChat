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
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.events.message.SendChannelMessageEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Messages;
import net.silthus.schat.message.Targets;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.policies.Policy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import static net.kyori.adventure.text.Component.text;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"key"})
@ToString(of = {"key", "settings", "targets"})
final class ChannelImpl implements Channel {

    @Setter
    private static Function<ChannelImpl.Builder, ChannelImpl.Builder> prototype = builder -> builder;

    static ChannelImpl.Builder builder(String key) {
        return prototype.apply(new Builder(key));
    }

    private static final String VALID_KEY_PATTERN = "^[a-zA-Z0-9_-]+$";

    private final String key;
    private final Settings settings;
    private final Targets targets;
    private final transient Messages messages = new Messages();
    private final transient EventBus eventBus;
    private final transient Map<Class<? extends Policy>, Policy> policies;

    private ChannelImpl(Builder builder) {
        this.key = builder.key;
        this.settings = builder.settings
            .withStatic(KEY, key)
            .withStatic(ChannelSettings.DISPLAY_NAME, builder.name)
            .create();
        this.targets = builder.targets;
        this.eventBus = builder.eventBus;
        this.policies = Map.copyOf(builder.policies);
    }

    public @NotNull @Unmodifiable Messages messages() {
        return Messages.unmodifiable(messages);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P extends Policy> Optional<P> policy(Class<P> policy) {
        return Optional.ofNullable((P) policies.get(policy));
    }

    @Override
    public @NotNull @Unmodifiable Targets targets() {
        return Targets.unmodifiable(targets);
    }

    @Override
    public void addTarget(MessageTarget user) {
        targets.add(user);
    }

    @Override
    public void removeTarget(MessageTarget target) {
        targets.remove(target);
    }

    @Override
    public void updateTargets() {
        for (MessageTarget target : targets())
            if (target instanceof Chatter chatter)
                chatter.join(this);
    }

    @Override
    public void sendMessage(@NonNull Message message) {
        if (messages.add(message))
            processMessage(message);
    }

    private void processMessage(Message message) {
        SendChannelMessageEvent event = eventBus.post(new SendChannelMessageEvent(this, message));
        if (event.isNotCancelled())
            event.targets().sendMessage(event.message());
    }

    @Override
    public int compareTo(@NotNull Channel o) {
        return Comparator.<Channel, Integer>comparing(o2 -> o2.get(ChannelSettings.PRIORITY))
            .thenComparing(Channel::key)
            .compare(this, o);
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
        public Channel.@NotNull Builder settings(@NonNull Settings settings) {
            this.settings = settings.toBuilder();
            return this;
        }

        @Override
        public Channel.Builder targets(Targets targets) {
            this.targets = targets;
            return this;
        }

        @Override
        public <P extends Policy> Channel.Builder policy(Class<P> type, P policy) {
            policies.put(type, policy);
            return this;
        }

        @Override
        public ChannelImpl create() {
            return new ChannelImpl(this);
        }

        private boolean isInvalidKey(String key) {
            return key == null || key.isBlank() || !key.matches(VALID_KEY_PATTERN);
        }
    }

}
