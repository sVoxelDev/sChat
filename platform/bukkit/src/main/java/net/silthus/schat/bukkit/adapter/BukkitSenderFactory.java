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
import net.silthus.schat.platform.plugin.adapter.SenderFactory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.identity.Identity.identity;

public final class BukkitSenderFactory extends SenderFactory<CommandSender> {

    private static final @NotNull LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    @Getter
    private final BukkitAudiences audienceProvider;

    public BukkitSenderFactory(BukkitAudiences audienceProvider) {
        this.audienceProvider = audienceProvider;
    }

    @NotNull
    protected Identity getIdentity(CommandSender sender) {
        if (sender instanceof Player player) {
            return identity(player.getUniqueId(),
                player.getName(),
                () -> LEGACY_SERIALIZER.deserialize(player.getDisplayName())
            );
        } else {
            return Identity.nil();
        }
    }

    @Override
    protected PermissionHandler getPermissionHandler(CommandSender sender) {
        return sender::hasPermission;
    }

    @Override
    protected MessageHandler getMessageHandler(CommandSender sender) {
        return message -> getAudienceProvider().sender(sender).sendMessage(message);
    }
}
