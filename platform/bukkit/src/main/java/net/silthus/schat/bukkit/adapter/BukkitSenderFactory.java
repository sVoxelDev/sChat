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

package net.silthus.schat.bukkit.adapter;

import java.util.UUID;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;
import net.silthus.schat.platform.sender.SenderFactory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

public final class BukkitSenderFactory extends SenderFactory<CommandSender> {

    private final BukkitAudiences audiences;
    private final SchedulerAdapter scheduler;

    public BukkitSenderFactory(BukkitAudiences audiences, SchedulerAdapter scheduler) {
        this.audiences = audiences;
        this.scheduler = scheduler;
    }

    @Override
    protected Class<CommandSender> senderType() {
        return CommandSender.class;
    }

    @Override
    protected Identity identity(CommandSender sender) {
        if (sender instanceof OfflinePlayer player)
            return BukkitIdentityAdapter.identity(player);
        return CONSOLE;
    }

    @Override
    protected void sendMessage(CommandSender sender, Component message) {
        if (canSendAsync(sender))
            audiences.sender(sender).sendMessage(message);
        else
            scheduler.executeSync(() -> this.audiences.sender(sender).sendMessage(message));
    }

    private boolean canSendAsync(CommandSender sender) {
        return sender instanceof Player || sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender;
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission(node);
    }

    @Override
    protected void performCommand(CommandSender sender, String command) {
        Bukkit.getServer().dispatchCommand(sender, command);
    }

    @Override
    protected boolean isConsole(CommandSender sender) {
        return sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender;
    }

    @Override
    public boolean isPlayerOnline(UUID playerId) {
        final Player player = Bukkit.getPlayer(playerId);
        return player != null && player.isOnline();
    }

    @Override
    public void close() {
        super.close();
        this.audiences.close();
    }
}
