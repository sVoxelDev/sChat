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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.Channels;
import net.silthus.schat.core.api.ApiChannelRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static net.kyori.adventure.text.Component.text;

public final class ChannelRepository {

    @Getter
    private final ApiChannelRepository apiProxy = new ApiChannelRepository(this);

    private final Map<String, Channel> channels = new HashMap<>();

    public @NotNull @Unmodifiable List<Channel> all() {
        return List.copyOf(channels.values());
    }

    public @NotNull Optional<Channel> get(final @NonNull String alias) {
        return Optional.ofNullable(channels.get(formatAlias(alias)));
    }

    public @NotNull Channel create(final @NonNull String alias) throws Channels.DuplicateAlias {
        return create(alias, text(alias));
    }

    public @NotNull Channel create(@NonNull final String alias, @NonNull final Component displayName) {
        if (contains(alias))
            throw new Channels.DuplicateAlias();
        return channels.computeIfAbsent(formatAlias(alias), c -> new Channel(c, displayName));
    }

    public boolean contains(final @NonNull String alias) {
        return channels.containsKey(formatAlias(alias));
    }

    @NotNull
    private String formatAlias(@NotNull String alias) {
        return alias.toLowerCase(Locale.ROOT);
    }

}
