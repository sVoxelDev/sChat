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

package net.silthus.chat.utils;

import net.silthus.chat.InstantiationException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public final class ReflectionUtil {

    private ReflectionUtil() {
    }

    @NotNull
    public static <TType> Supplier<TType> getDefaultSupplier(Class<TType> typeClass) {
        try {
            final Constructor<TType> constructor = typeClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return () -> {
                try {
                    return constructor.newInstance();
                } catch (java.lang.InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new InstantiationException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new InstantiationException(e);
        }
    }
}
