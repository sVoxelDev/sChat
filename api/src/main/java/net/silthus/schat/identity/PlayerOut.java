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
import org.jetbrains.annotations.NotNull;

/**
 * An adapter that can convert between identities to platform native players.
 *
 * @param <P> the type of the player on the implementing platform
 * @since next
 */
public interface PlayerOut<P> {

    /**
     * Converts the given identity into a player.
     *
     * @param identity the identity
     * @return the player for the given identity
     * @since next
     */
    @NotNull Optional<P> toPlayer(@NotNull Identity identity);
}
