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

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.pointer.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import static net.kyori.adventure.text.Component.text;

@Getter
@EqualsAndHashCode(of = {"key"})
final class ChannelImpl implements Channel {

    private static final String VALID_KEY_PATTERN = "^[a-zA-Z0-9_-]+$";

    private final String key;
    private final Component displayName;
    private final Settings settings;
    private final Set<MessageTarget> targets = new HashSet<>();

    private ChannelImpl(Builder builder) {
        this.key = builder.key;
        this.displayName = builder.displayName;
        this.settings = builder.settings.create();
    }

    @Override
    public @NotNull @Unmodifiable List<MessageTarget> getTargets() {
        return List.copyOf(targets);
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
    public int compareTo(@NotNull Channel o) {
        return Comparator.<Channel, Integer>comparing(o2 -> o2.get(PRIORITY))
            .thenComparing(Channel::getKey)
            .compare(this, o);
    }

    static final class Builder implements Channel.Builder {

        private final String key;
        private Component displayName;
        private Settings.Builder settings = Settings.settings();

        Builder(String key) {
            if (isInvalidKey(key))
                throw new InvalidKey();

            this.key = key;
            this.displayName = text(key);
        }

        @Override
        public Builder name(Component displayName) {
            this.displayName = displayName;
            return this;
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
        public ChannelImpl create() {
            return new ChannelImpl(this);
        }

        private boolean isInvalidKey(String key) {
            return key == null || key.isBlank() || !key.matches(VALID_KEY_PATTERN);
        }
    }

}
