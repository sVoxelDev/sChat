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
    private final Settings settings;
    private final Set<MessageTarget> targets = new HashSet<>();

    private ChannelImpl(Builder builder) {
        this.key = builder.key;
        this.settings = builder.settings.withStatic(DISPLAY_NAME, builder.displayName).create();
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
