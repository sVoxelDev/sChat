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

package net.silthus.schat.platform.plugin.bootstrap;

import java.time.Instant;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Provides information about the platform sChat is running on.
 */
public interface Platform {

    /**
     * Gets the type of platform sChat is running on.
     *
     * @return the type of platform sChat is running on
     */
    @NonNull Type getType();

    /**
     * Gets the time when the plugin first started.
     *
     * @return the enable time
     */
    @NonNull Instant getStartTime();

    /**
     * Represents a type of platform which sChat can run on.
     */
    enum Type {
        BUKKIT("Bukkit"),
        BUNGEECORD("BungeeCord"),
        SPONGE("Sponge"),
        NUKKIT("Nukkit"),
        VELOCITY("Velocity"),
        FABRIC("Fabric"),
        MINESTOM("Minestom");

        private final String friendlyName;

        Type(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        /**
         * Gets a readable name for the platform type.
         *
         * @return a readable name
         */
        public @NonNull String getFriendlyName() {
            return this.friendlyName;
        }
    }
}
