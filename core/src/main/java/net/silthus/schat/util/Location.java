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
package net.silthus.schat.util;

import java.util.UUID;
import net.silthus.schat.pointer.Pointer;

/**
 * Represents a typical Minecraft location object.
 *
 * <p>Use the helper inside the implementing platform to wrap and unwrap native location objects.</p>
 *
 * @param world the world of the location
 * @param x x-axis
 * @param y y-axis
 * @param z z-axis
 * @param yaw horizontal rotation
 * @param pitch vertical rotation
 * @since next
 */
public record Location(World world, double x, double y, double z, float yaw, float pitch) {

    public static final Pointer<Location> LOCATION = Pointer.pointer(Location.class, "location");

    public Location(World world, double x, double y, double z) {
        this(world, x, y, z, 0f, 90f);
    }

    /**
     * Represents a typical Minecraft world identified by an id and name.
     *
     * @param id the unique id of the world
     * @param name the name of the world
     * @since next
     */
    public record World(UUID id, String name) {
    }
}
