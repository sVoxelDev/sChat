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
import net.silthus.chat.utils.AnnotationUtils;
import net.silthus.chat.utils.ReflectionUtil;
import net.silthus.configmapper.bukkit.BukkitConfigMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Log(topic = Constants.PLUGIN_NAME)
public final class Scopes {

    private static final Map<String, RegisteredScope<?>> scopes = new HashMap<>();

    static {
        register(GlobalScope.class);
        register(ServerScope.class);
        register(WorldScope.class);
        register(LocalScope.class);
    }

    public static ServerScope server() {
        return scope(ServerScope.class);
    }

    public static GlobalScope global() {
        return scope(GlobalScope.class);
    }

    public static LocalScope local() {
        return scope(LocalScope.class);
    }

    public static <TScope extends Scope> TScope scope(Class<TScope> scopeClass) {
        return scopeClass.cast(scope(AnnotationUtils.name(scopeClass)));
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
        String name = AnnotationUtils.name(scope);
        Supplier<TScope> supplier = ReflectionUtil.getDefaultSupplier(scope);
        RegisteredScope<?> oldScope = scopes.put(name, new RegisteredScope<>(name, scope, supplier));
        if (oldScope != null)
            log.warning("Existing scope " + oldScope.scopeClass().getCanonicalName() + " with key '"
                    + name + "' was replaced by " + scope.getCanonicalName());
    }

    private Scopes() {
    }

    private static record RegisteredScope<TScope extends Scope>(String name, Class<TScope> scopeClass,
                                                                Supplier<TScope> supplier) {
    }
}
