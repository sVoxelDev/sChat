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
import net.kyori.adventure.text.Component;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;
import net.silthus.schat.sender.Sender;
import net.silthus.schat.sender.SenderFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;

public final class BukkitSenderFactory extends SenderFactory<CommandSender> {

    private final BukkitAudiences audiences;
    private final SchedulerAdapter scheduler;

    public BukkitSenderFactory(BukkitAudiences audiences, SchedulerAdapter scheduler) {
        this.audiences = audiences;
        this.scheduler = scheduler;
    }

    @Override
    protected Class<CommandSender> getSenderType() {
        return CommandSender.class;
    }

    @Override
    protected UUID getUniqueId(CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getUniqueId();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    protected String getName(CommandSender sender) {
        if (sender instanceof Player) {
            return sender.getName();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    protected Component getDisplayName(CommandSender sender) {
        if (sender instanceof Player player)
            return text(player.getDisplayName());
        return text(Sender.CONSOLE_NAME);
    }

    @Override
    protected void sendMessage(CommandSender sender, Component message) {
        // we can safely send async for players and the console - otherwise, send it sync
        if (sender instanceof Player || sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {
            this.audiences.sender(sender).sendMessage(message);
        } else {
            scheduler.executeSync(() -> this.audiences.sender(sender).sendMessage(message));
        }
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
    public Optional<Sender> getSender(UUID playerId) {
        return Optional.ofNullable(Bukkit.getPlayer(playerId)).map(this::adapt);
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
