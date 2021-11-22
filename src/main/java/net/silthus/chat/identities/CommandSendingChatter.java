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

import net.kyori.adventure.audience.Audience;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.UUID;

public final class CommandSendingChatter extends AbstractChatter {

    private final CommandSender sender;

    public CommandSendingChatter(UUID id, CommandSender sender) {
        super(id, sender.getName());
        this.sender = sender;
    }

    @Override
    protected void processMessage(Message message) {
        getAudience().ifPresent(audience -> audience.sendMessage(message.formatted()));
    }

    @Override
    public Optional<Audience> getAudience() {
        return Optional.of(SChat.instance().getAudiences().sender(sender));
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void save() {

    }

    @Override
    public void load() {

    }
}
