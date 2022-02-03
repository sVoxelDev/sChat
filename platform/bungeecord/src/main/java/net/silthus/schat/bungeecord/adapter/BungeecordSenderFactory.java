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

import java.util.UUID;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.platform.sender.SenderFactory;

import static net.silthus.schat.bungeecord.adapter.BungeecordIdentityAdapter.identity;

public class BungeecordSenderFactory extends SenderFactory<CommandSender> {

    private final Plugin plugin;
    private final BungeeAudiences audiences;

    public BungeecordSenderFactory(Plugin plugin) {
        audiences = BungeeAudiences.create(plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean isPlayerOnline(UUID playerId) {
        return plugin.getProxy().getPlayer(playerId) != null;
    }

    @Override
    protected Class<CommandSender> getSenderType() {
        return CommandSender.class;
    }

    @Override
    protected Identity getIdentity(CommandSender sender) {
        if (sender instanceof ProxiedPlayer player)
            return identity(player);
        else
            return CONSOLE;
    }

    @Override
    protected void sendMessage(CommandSender sender, Component message) {
        audiences.sender(sender).sendMessage(message);
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission(node);
    }

    @Override
    protected void performCommand(CommandSender sender, String command) {
        plugin.getProxy().getPluginManager().dispatchCommand(sender, command);
    }

    @Override
    protected boolean isConsole(CommandSender sender) {
        return !(sender instanceof ProxiedPlayer);
    }
}
