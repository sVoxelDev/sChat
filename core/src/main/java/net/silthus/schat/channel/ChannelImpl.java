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

package net.silthus.schat.channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.Check;
import net.silthus.schat.channel.checks.JoinChannelPermissionCheck;
import net.silthus.schat.channel.usecases.JoinChannel;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Messages;
import net.silthus.schat.message.messenger.Messenger;
import net.silthus.schat.settings.Setting;
import net.silthus.schat.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.permission.Permission.of;

final class ChannelImpl implements Channel {

    private static final Pattern CHANNEL_KEY_PATTERN = Pattern.compile("^[a-z0-9_-]+$");
    private static final Messenger<Channel> DEFAULT_MESSENGER = Messenger.messenger(new DefaultChannelStrategy());

    @Getter
    private final String key;
    private final Set<MessageTarget> targets = new HashSet<>();
    private final Messenger<Channel> messenger;
    @Getter
    private final Settings settings;
    private final Map<Class<? extends Check>, List<? extends Check>> checks;

    private ChannelImpl(ChannelImplBuilder builder) {
        this.key = builder.key;
        this.messenger = builder.messenger;
        this.settings = builder.settings.create();
        this.checks = builder.checks;
    }

    @Override
    public @NotNull @Unmodifiable Set<MessageTarget> getTargets() {
        return Collections.unmodifiableSet(targets);
    }

    @Override
    public @NotNull @Unmodifiable Messages getMessages() {
        return messenger.getMessages();
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull @Unmodifiable <T extends Check> Collection<T> getChecks(Class<T> checkType) {
        return Collections.unmodifiableList((List<T>) this.checks.getOrDefault(checkType, new ArrayList<>()));
    }

    @Override
    public @NotNull Component getDisplayName() {
        return getOrDefault(DISPLAY_NAME, text(key));
    }

    @Override
    public void addTarget(final @NonNull MessageTarget target) {
        this.targets.add(target);
    }

    @Override
    public void sendMessage(final @NonNull Message message) {
        messenger.sendMessage(this, message);
    }

    private static class DefaultChannelStrategy implements Messenger.Strategy<Channel> {

        @Override
        public void deliver(final @NotNull Message message, final Messenger.@NotNull Context<Channel> context) {
            context.target().getTargets().forEach(messageTarget -> messageTarget.sendMessage(message));
        }
    }

    static class ChannelImplBuilder implements Builder {

        private final String key;
        private final Map<Class<? extends Check>, List<? extends Check>> checks = new HashMap<>(Map.of(
            JoinChannel.Check.class, List.of(
                new JoinChannelPermissionCheck()
            )
        ));
        private Settings.Builder settings;
        private Messenger<Channel> messenger = DEFAULT_MESSENGER;

        ChannelImplBuilder(String key) {
            if (isInvalidChannelKey(key))
                throw new InvalidKey();
            this.key = key;
            defaultSettings(Settings.settings()
                .withStatic(JOIN_PERMISSION, of("schat.channel." + key + ".join")));
        }

        @Override
        public <V> @NotNull Builder setting(final @NonNull Setting<V> setting, final @Nullable V value) {
            this.settings.withStatic(setting, value);
            return this;
        }

        @Override
        public @NotNull Builder defaultSettings(final @NonNull Settings.Builder settings) {
            this.settings = settings;
            return this;
        }

        @Override
        public @NotNull Builder settings(final @NonNull Settings settings) {
            this.settings = settings.copy();
            return this;
        }

        @Override
        public @NotNull Builder settings(final @NonNull Consumer<Settings.Builder> settings) {
            settings.accept(this.settings);
            return this;
        }

        @Override
        public Builder displayName(@NonNull Component displayName) {
            if (displayName.equals(empty()))
                return this;
            return setting(DISPLAY_NAME, displayName);
        }

        @Override
        public Builder messenger(@NonNull Messenger<Channel> messenger) {
            this.messenger = messenger;
            return this;
        }

        @Override
        public Builder clearChecks() {
            this.checks.clear();
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Check> Builder check(T @NonNull ... checks) {
            if (checks.length > 1) return this;
            final List<T> checklist = (List<T>) this.checks.computeIfAbsent(checks[0].getClass(), c -> new ArrayList<>());
            checklist.addAll(List.of(checks));
            return this;
        }

        @Override
        public Channel create() {
            return new ChannelImpl(this);
        }

        private boolean isInvalidChannelKey(final String key) {
            return CHANNEL_KEY_PATTERN.asMatchPredicate().negate().test(key);
        }
    }
}
