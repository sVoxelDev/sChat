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

import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silthus.schat.chatter.MessageHandler;
import net.silthus.schat.chatter.PermissionHandler;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.platform.sender.ChatterFactory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.identity.Identity.identity;

public final class BukkitChatterFactory extends ChatterFactory<Player> {

    private static final @NotNull LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    @Getter
    private final BukkitAudiences audienceProvider;

    public BukkitChatterFactory(BukkitAudiences audienceProvider) {
        this.audienceProvider = audienceProvider;
    }

    @Override
    protected Class<Player> getType() {
        return Player.class;
    }

    @NotNull
    protected Identity getIdentity(Player player) {
        return identity(player.getUniqueId(),
            player.getName(),
            () -> LEGACY_SERIALIZER.deserialize(player.getDisplayName())
        );
    }

    @Override
    protected PermissionHandler getPermissionHandler(Player player) {
        return player::hasPermission;
    }

    @Override
    protected MessageHandler getMessageHandler(Player player) {
        return message -> getAudienceProvider().player(player).sendMessage(message);
    }
}
