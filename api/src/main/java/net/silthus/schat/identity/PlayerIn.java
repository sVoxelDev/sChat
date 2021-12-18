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

package net.silthus.schat.identity;

import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * An adapter that can convert between platform native players to identities.
 *
 * @param <P> the type of the player on the implementing platform
 * @since next
 */
public interface PlayerIn<P> {

    /**
     * Converts the given player into an identity.
     *
     * @param player the player
     * @return the identity of the player
     * @since next
     */
    @NotNull Identity fromPlayer(@NotNull P player);

    /**
     * Tries to find a player by the given id and converts it into an identity.
     *
     * @param playerId the player id
     * @return the identity of the player if found
     * @since next
     */
    @NotNull Optional<Identity> fromId(UUID playerId);
}
