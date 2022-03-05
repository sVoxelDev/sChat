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
    @NonNull Type type();

    /**
     * Gets the time when the plugin first started.
     *
     * @return the enable time
     */
    @NonNull Instant startTime();

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
        public @NonNull String friendlyName() {
            return this.friendlyName;
        }
    }
}
