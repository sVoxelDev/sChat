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

package net.silthus.schat.core.channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.core.repository.Entity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.target.MessageTarget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class ChannelEntity implements Channel, Entity<String> {

    private static final Pattern VALID_CHANNEL_ID = Pattern.compile("^[a-z0-9_-]+$");

    @Getter
    private final @NotNull String key;
    private final @NotNull List<MessageTarget> targets = new ArrayList<>();

    @Getter
    @Setter
    private @NonNull Component displayName;

    public ChannelEntity(@NotNull String key, @NotNull Component displayName) {
        if (isInvalidChannelKey(key))
            throw new Channel.InvalidKey();
        this.key = key;
        this.displayName = displayName;
    }

    public void sendMessage(final @NonNull Message message) {
        getTargets().forEach(target -> target.sendMessage(message));
    }

    @NotNull @Unmodifiable
    public List<MessageTarget> getTargets() {
        return Collections.unmodifiableList(targets);
    }

    public void addTarget(final @NonNull MessageTarget target) {
        this.targets.add(target);
    }

    public void removeTarget(final @NonNull MessageTarget target) {
        this.targets.remove(target);
    }

    private boolean isInvalidChannelKey(final String key) {
        return VALID_CHANNEL_ID.asMatchPredicate().negate().test(key);
    }

}
