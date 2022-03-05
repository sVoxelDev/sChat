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
package net.silthus.schat.velocity.adapter;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.platform.sender.SenderFactory;

public final class VelocitySenderFactory extends SenderFactory<CommandSource> {

    private final ProxyServer proxy;

    public VelocitySenderFactory(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean isPlayerOnline(UUID playerId) {
        return proxy.getPlayer(playerId).isPresent();
    }

    @Override
    protected Class<CommandSource> senderType() {
        return CommandSource.class;
    }

    @Override
    protected Identity identity(CommandSource sender) {
        if (sender instanceof Player player)
            return identity(player);
        return CONSOLE;
    }

    public static Identity identity(Player player) {
        return Identity.identity(player.getUniqueId(), player.getUsername());
    }

    @Override
    protected void sendMessage(CommandSource sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    protected void sendActionBar(CommandSource sender, Component message) {
        sender.sendActionBar(message);
    }

    @Override
    protected boolean hasPermission(CommandSource sender, String node) {
        return sender.hasPermission(node);
    }

    @Override
    protected void performCommand(CommandSource sender, String command) {
        proxy.getCommandManager().executeAsync(sender, command);
    }

    @Override
    protected boolean isConsole(CommandSource sender) {
        return sender instanceof ConsoleCommandSource;
    }
}
