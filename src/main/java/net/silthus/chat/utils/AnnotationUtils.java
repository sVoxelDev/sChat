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

import net.silthus.chat.annotations.Name;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AnnotationUtils {

    public static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("(?!^)(?=[A-Z][a-z])");

    @NotNull
    public static String name(Class<?> scope) {
        if (scope.isAnnotationPresent(Name.class))
            return scope.getAnnotation(Name.class).value();
        else
            return getNameFromClass(scope);
    }

    @NotNull
    public static String getNameFromClass(Class<?> scope) {
        Matcher matcher = CAMEL_CASE_PATTERN.matcher(getClassName(scope));
        return matcher.replaceAll("-").toLowerCase();
    }

    @NotNull
    public static String getClassName(Class<?> scope) {
        final List<String> names = Arrays.stream(scope.getInterfaces())
                .map(Class::getSimpleName)
                .toList();
        String name = scope.getSimpleName();
        for (String superName : names) {
            name = name.replace(superName, "");
        }
        return name;
    }

    private AnnotationUtils() {
    }
}
