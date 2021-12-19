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

package net.silthus.schat.bukkit;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.silthus.schat.core.Sender;
import net.silthus.schat.core.SenderFactory;
import net.silthus.schat.identity.PlayerAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BukkitAudiencesSenderFactory extends SenderFactory<Player> {

    private final BukkitAudiences audiences;

    public BukkitAudiencesSenderFactory(final PlayerAdapter<Player> playerAdapter, final @NotNull BukkitAudiences audiences) {
        super(playerAdapter);
        this.audiences = audiences;
    }

    @Override
    protected Sender.SendMessage<Player> sendMessage() {
        return (sender, component) -> audiences.player(sender).sendMessage(component);
    }
}
