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

package net.silthus.schat.message;

import java.util.Collection;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.identity.Identity;

import static net.silthus.schat.message.NewMessage.message;

@Setter
@Accessors(fluent = true)
public class Messenger {
    private MessageOut out;

    public void sendMessageTo(@NonNull Component message, @NonNull Identity identity) {
        out.onMessageSent(message(identity, message));
    }

    public void sendMessageTo(@NonNull Component message, @NonNull Collection<Identity> targets) {
        for (final Identity target : targets) {
            if (target != null)
                sendMessageTo(message, target);
        }
    }

    public void sendMessageTo(Component text, Channel channel) {
        for (final MessageTarget target : channel.getTargets()) {
            if (target instanceof Identified identified)
                sendMessageTo(text, identified);
        }
    }
}

