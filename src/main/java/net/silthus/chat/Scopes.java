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

package net.silthus.chat;

import lombok.extern.java.Log;
import net.silthus.chat.scopes.GlobalScope;
import net.silthus.chat.scopes.ServerScope;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log(topic = Constants.PLUGIN_NAME)
public final class Scopes {

    private static final Map<String, Scope> scopes = new HashMap<>();

    static {
        register(new GlobalScope());
        register(new ServerScope());
    }

    public static Scope server() {
        return new ServerScope();
    }

    public static Collection<Scope> scopes() {
        return List.copyOf(scopes.values());
    }

    public static Scope scope(String scope) {
        if (scope == null) return null;
        return scopes.get(scope.toLowerCase());
    }

    public static void register(Scope scope) {
        String name = getName(scope);
        Scope oldScope = scopes.put(name, scope);
        if (oldScope != null)
            log.warning("Existing scope " + oldScope.getClass().getCanonicalName() + " with key '"
                    + name + "' was replaced by " + scope.getClass().getCanonicalName());
    }

    private static String getName(Scope scope) {
        if (scope.getClass().isAnnotationPresent(Scope.Name.class))
            return scope.getClass().getAnnotation(Scope.Name.class).value();
        else
            return getNameFromClass(scope);
    }

    private final static Pattern CAMEL_CASE_PATTERN = Pattern.compile("(?!^)(?=[A-Z][a-z])");

    @NotNull
    private static String getNameFromClass(Scope scope) {
        Matcher matcher = CAMEL_CASE_PATTERN.matcher(getClassName(scope));
        return matcher.replaceAll("-").toLowerCase();
    }

    @NotNull
    private static String getClassName(Scope scope) {
        return scope.getClass().getSimpleName().replace("Scope", "");
    }

    private Scopes() {
    }
}
