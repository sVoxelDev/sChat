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

package net.silthus.schat.bukkit;

import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.identity.PlayerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BukkitPlayerAdapter implements PlayerAdapter<Player> {

    @NotNull
    public static Identity createPlayerIdentity(final @NotNull Player player) {
        return Identity.identity(
            player.getUniqueId(),
            player.getName(),
            () -> LEGACY_SERIALIZER.deserialize(player.getDisplayName())
        );
    }

    public static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    @Override
    public @NotNull Identity fromPlayer(@NotNull final Player player) {
        return createPlayerIdentity(player);
    }

    @Override
    public @NotNull Optional<Identity> fromId(final UUID playerId) {
        final Player player = Bukkit.getPlayer(playerId);
        if (player == null) return Optional.empty();
        return Optional.of(fromPlayer(player));
    }

    @Override
    public @NotNull Optional<Player> toPlayer(@NotNull final Identity identity) {
        return Optional.ofNullable(Bukkit.getPlayer(identity.getId()));
    }
}
