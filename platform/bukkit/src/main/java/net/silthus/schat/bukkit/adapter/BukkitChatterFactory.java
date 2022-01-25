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

package net.silthus.schat.bukkit.adapter;

import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.view.Display;
import net.silthus.schat.view.ViewProvider;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.bukkit.adapter.BukkitIdentityAdapter.identity;
import static org.bukkit.Bukkit.getOfflinePlayer;

public final class BukkitChatterFactory extends ChatterFactory {

    private final BukkitAudiences audiences;

    public BukkitChatterFactory(BukkitAudiences audiences, ViewProvider viewProvider) {
        super(viewProvider);
        this.audiences = audiences;
    }

    @Override
    @NotNull
    protected Identity getIdentity(UUID id) {
        return identity(getOfflinePlayer(id));
    }

    @Override
    protected Chatter.PermissionHandler getPermissionHandler(UUID id) {
        return permission -> Optional.ofNullable(Bukkit.getPlayer(id))
            .map(player -> player.hasPermission(permission))
            .orElse(false);
    }

    @Override
    protected Display getDisplay(UUID id) {
        return renderedView -> audiences.player(id).sendMessage(renderedView);
    }
}
