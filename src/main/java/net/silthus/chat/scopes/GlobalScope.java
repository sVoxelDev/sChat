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

package net.silthus.chat.scopes;

import lombok.EqualsAndHashCode;
import net.silthus.chat.*;
import net.silthus.chat.conversations.Channel;

import java.util.Collection;
import java.util.HashSet;

@EqualsAndHashCode
@Scope.Name(Constants.Scopes.GLOBAL)
public final class GlobalScope implements Scope {

    @Override
    public Collection<ChatTarget> apply(Channel channel, Message message) {
        HashSet<ChatTarget> chatTargets = new HashSet<>(channel.getTargets());
        chatTargets.add(SChat.instance().getBungeecord());
        return chatTargets;
    }
}
