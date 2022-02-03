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

package net.silthus.schat.bungeecord.adapter;

import java.util.function.Supplier;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.silthus.schat.identity.Identity;
import org.jetbrains.annotations.NotNull;

public final class BungeecordIdentityAdapter {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    private BungeecordIdentityAdapter() {
    }

    public static @NotNull Identity identity(@NonNull ProxiedPlayer player) {
        return Identity.identity(
            player.getUniqueId(),
            player.getName(),
            displayName(player)
        );
    }

    @NotNull
    private static Supplier<Component> displayName(@NonNull ProxiedPlayer player) {
        return () -> LEGACY_SERIALIZER.deserialize(player.getDisplayName());
    }
}
