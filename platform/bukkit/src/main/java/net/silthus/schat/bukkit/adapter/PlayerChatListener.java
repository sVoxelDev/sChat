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

import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.commands.ChatCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.commands.ChatCommand.chat;
import static net.silthus.schat.platform.locale.Messages.CANNOT_CHAT_NO_ACTIVE_CHANNEL;

public final class PlayerChatListener implements Listener {

    private final ChatterProvider chatterProvider;

    public PlayerChatListener(ChatterProvider provider) {
        chatterProvider = provider;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        final Chatter chatter = chatterProvider.get(event.getPlayer().getUniqueId());
        tryChatting(chatter, event.getMessage());
        event.setCancelled(true);
    }

    private void tryChatting(Chatter chatter, String message) {
        try {
            chat(chatter, text(message));
        } catch (ChatCommand.NoActiveChannel e) {
            CANNOT_CHAT_NO_ACTIVE_CHANNEL.send(chatter);
        }
    }
}
