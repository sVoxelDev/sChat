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

package net.silthus.schat.bukkit.protocollib;

import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.SenderChatterLookup;
import net.silthus.schat.message.Message;
import net.silthus.schat.sender.PlayerAdapter;
import net.silthus.schat.sender.Sender;
import net.silthus.schat.ui.View;
import net.silthus.schat.ui.ViewProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ChatPacketProcessor {
    private final PlayerAdapter<CommandSender> playerAdapter;
    private final SenderChatterLookup chatterLookup;
    private final ViewProvider viewProvider;

    public ChatPacketProcessor(PlayerAdapter<CommandSender> playerAdapter, SenderChatterLookup chatterLookup, ViewProvider viewProvider) {
        this.playerAdapter = playerAdapter;
        this.chatterLookup = chatterLookup;
        this.viewProvider = viewProvider;
    }

    Component processMessage(Player player, Component rawMessage) {
        if (ignoredOrAlreadyProcessed(rawMessage)) return rawMessage;

        final Sender sender = playerAdapter.adapt(player);
        final Chatter chatter = chatterLookup.getChatter(sender);
        chatter.addMessage(Message.message(rawMessage));
        return viewProvider.getView(sender).render();
    }

    boolean ignoredOrAlreadyProcessed(final @Nullable Component rawMessage) {
        if (rawMessage == null) return true;
        return rawMessage.contains(View.MESSAGE_MARKER) || rawMessage.children().contains(View.MESSAGE_MARKER);
    }
}
