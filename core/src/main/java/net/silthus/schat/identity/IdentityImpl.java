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

import java.util.UUID;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;

/**
 * Holds the id, name and display name of an identified entity.
 *
 * <p>Identities could be players, channels, the console and so on.
 * If the identity is a player the id of the player in the platform matches the identity's id.</p>
 *
 * @since next
 */
@Getter
@EqualsAndHashCode(of = {"uniqueId"})
final class IdentityImpl implements Identity {

    static final Identity NIL = Identity.identity("", Component.empty());

    private final @NonNull UUID uniqueId;
    private final @NonNull String name;
    private final @NonNull Supplier<Component> displayName;

    IdentityImpl(
        @NonNull UUID uniqueId,
        @NonNull String name,
        @NonNull Supplier<Component> displayName
    ) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.displayName = displayName;
    }

    /**
     * Gets the display of the identity.
     *
     * @return the display name
     * @since next
     */
    @Override
    public Component getDisplayName() {
        return displayName.get();
    }
}
