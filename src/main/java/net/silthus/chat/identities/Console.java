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

package net.silthus.chat.identities;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.chat.*;
import net.silthus.chat.config.ConsoleConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class Console extends AbstractChatTarget implements ChatSource, Listener {

    public static Console instance;

    public static Console console() {
        if (instance == null)
            throw new UnsupportedOperationException("The console chat target is not initialized! Is sChat enabled?");
        return instance;
    }

    public static Console init(@NonNull ConsoleConfig config) {
        if (instance != null)
            throw new UnsupportedOperationException("The console chat target is already initialized. Can only initialize once!");
        instance = new Console(config);
        return instance;
    }

    @Getter(AccessLevel.PACKAGE)
    private final ChatTarget target;

    private Console(ConsoleConfig config) {
        super(Constants.Targets.CONSOLE);
        setDisplayName(Bukkit.getConsoleSender().name());
        this.target = SChat.instance().getChannelRegistry().find(config.defaultChannel()).orElse(null);
    }

    @Override
    public void sendMessage(Message message) {
        Bukkit.getConsoleSender().sendMessage(message.formatted());
        addReceivedMessage(message);
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsoleChat(ServerCommandEvent event) {
        if (event.getCommand().startsWith("/")) return;

        sendMessage(message(event.getCommand()).to(getTarget()).send());
    }
}
