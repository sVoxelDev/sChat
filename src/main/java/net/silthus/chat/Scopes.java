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
import net.silthus.chat.scopes.LocalScope;
import net.silthus.chat.scopes.ServerScope;
import net.silthus.chat.scopes.WorldScope;
import net.silthus.configmapper.bukkit.BukkitConfigMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log(topic = Constants.PLUGIN_NAME)
public final class Scopes {

    private static final Map<String, RegisteredScope<?>> scopes = new HashMap<>();

    static {
        register(GlobalScope.class);
        register(ServerScope.class);
        register(WorldScope.class);
        register(LocalScope.class);
    }

    public static Scope server() {
        return new ServerScope();
    }

    public static Scope scope(String scope) {
        return scope(scope, new MemoryConfiguration());
    }

    public static Scope scope(String scope, ConfigurationSection config) {
        if (scope == null) return null;
        RegisteredScope<?> registeredScope = scopes.get(scope.toLowerCase());
        if (registeredScope == null) return null;
        return BukkitConfigMap.of(registeredScope.supplier().get())
                .with(config)
                .apply();
    }

    public static <TScope extends Scope> void register(Class<TScope> scope) {
        String name = getName(scope);
        Supplier<TScope> supplier = getScopeSupplier(scope);
        RegisteredScope<?> oldScope = scopes.put(name, new RegisteredScope<>(name, scope, supplier));
        if (oldScope != null)
            log.warning("Existing scope " + oldScope.scopeClass().getCanonicalName() + " with key '"
                    + name + "' was replaced by " + scope.getCanonicalName());
    }

    @NotNull
    private static <TScope extends Scope> Supplier<TScope> getScopeSupplier(Class<TScope> scope) {
        try {
            final Constructor<TScope> constructor = scope.getDeclaredConstructor();
            constructor.setAccessible(true);
            return () -> {
                try {
                    return constructor.newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new ScopeInstantiationException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new ScopeInstantiationException(e);
        }
    }

    private static String getName(Class<? extends Scope> scope) {
        if (scope.isAnnotationPresent(Scope.Name.class))
            return scope.getAnnotation(Scope.Name.class).value();
        else
            return getNameFromClass(scope);
    }

    private final static Pattern CAMEL_CASE_PATTERN = Pattern.compile("(?!^)(?=[A-Z][a-z])");

    @NotNull
    private static String getNameFromClass(Class<? extends Scope> scope) {
        Matcher matcher = CAMEL_CASE_PATTERN.matcher(getClassName(scope));
        return matcher.replaceAll("-").toLowerCase();
    }

    @NotNull
    private static String getClassName(Class<? extends Scope> scope) {
        return scope.getSimpleName().replace("Scope", "");
    }

    private Scopes() {
    }

    private static record RegisteredScope<TScope extends Scope>(String name, Class<TScope> scopeClass,
                                                                Supplier<TScope> supplier) {

    }
}
