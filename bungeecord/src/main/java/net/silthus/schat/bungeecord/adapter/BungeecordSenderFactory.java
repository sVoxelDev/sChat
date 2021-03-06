/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
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
    protected Class<CommandSender> senderType() {
        return CommandSender.class;
    }

    @Override
    protected Identity identity(CommandSender sender) {
        if (sender instanceof ProxiedPlayer player)
            return BungeecordIdentityAdapter.identity(player);
        else
            return CONSOLE;
    }

    @Override
    protected void sendMessage(CommandSender sender, Component message) {
        audiences.sender(sender).sendMessage(message);
    }

    @Override
    protected void sendActionBar(CommandSender sender, Component message) {
        audiences.sender(sender).sendActionBar(message);
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
