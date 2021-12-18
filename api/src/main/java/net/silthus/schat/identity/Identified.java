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

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Marks an entity as identified requiring it to have an {@link Identity}.
 *
 * @since next
 */
public interface Identified {

    /**
     * Gets the identity of the identified entity.
     *
     * @return the identity
     * @since next
     */
    @NotNull Identity getIdentity();

    /**
     * Gets the id of this entity.
     *
     * @return the id
     * @since next
     */
    default @NotNull java.util.UUID getId() {
        return getIdentity().getId();
    }

    /**
     * Gets the name of this entity.
     *
     * @return the name
     * @since next
     */
    default @NotNull String getName() {
        return getIdentity().getName();
    }

    /**
     * Gets the display name of this entity.
     *
     * @return the display name
     * @since next
     */
    default @NotNull Component getDisplayName() {
        return getIdentity().getDisplayName();
    }
}
