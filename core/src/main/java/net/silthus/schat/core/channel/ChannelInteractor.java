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

import java.util.Collection;
import java.util.Optional;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.Channels;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static net.kyori.adventure.text.Component.text;

public final class ChannelInteractor implements Channels {

    private final ChannelRepository repository;

    public ChannelInteractor(ChannelRepository repository) {
        this.repository = repository;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public @NotNull @Unmodifiable Collection<Channel> all() {
        return (Collection) repository.all();
    }

    @Override
    public @NotNull Optional<Channel> get(@NonNull String alias) {
        return repository.get(alias).map(channelEntity -> channelEntity);
    }

    @Override
    public boolean contains(@NonNull String alias) {
        return repository.contains(alias);
    }

    @Override
    public @NotNull ChannelEntity create(@NonNull String alias) throws DuplicateIdentifier {
        return create(alias, text(alias));
    }

    @Override
    public @NotNull ChannelEntity create(@NonNull String alias, @NonNull Component displayName) {
        if (contains(alias))
            throw new DuplicateIdentifier();
        return repository.get(alias)
            .orElseGet(() -> createAndAddChannel(alias, displayName));
    }

    private @NotNull ChannelEntity createAndAddChannel(final @NotNull String alias, final @NotNull Component displayName) {
        final ChannelEntity channel = new ChannelEntity(alias, displayName);
        repository.add(channel);
        return channel;
    }
}
