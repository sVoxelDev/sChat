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

import java.util.UUID;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.view.ViewProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.silthus.schat.bukkit.adapter.BukkitIdentityAdapter.identity;
import static net.silthus.schat.chatter.Chatter.chatter;
import static net.silthus.schat.view.ViewConnector.createSimpleViewConnector;
import static org.bukkit.Bukkit.getOfflinePlayer;

public final class BukkitChatterFactory implements ChatterFactory {

    private final BukkitAudiences audiences;
    private final ViewProvider viewProvider;

    public BukkitChatterFactory(BukkitAudiences audiences, ViewProvider viewProvider) {
        this.audiences = audiences;
        this.viewProvider = viewProvider;
    }

    @Override
    public Chatter createChatter(UUID id) {
        return chatter(identity(getOfflinePlayer(id)))
            .viewConnector(createSimpleViewConnector(
                viewProvider,
                (chatter, renderedView) -> display(id, renderedView)
            ))
            .permissionHandler(permission -> {
                final Player player = Bukkit.getPlayer(id);
                return player != null && player.hasPermission(permission);
            })
            .create();
    }

    private void display(UUID id, Component renderedView) {
        audiences.player(id).sendMessage(renderedView);
    }
}
