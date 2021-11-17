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

import lombok.Data;
import lombok.experimental.Accessors;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Message;
import net.silthus.chat.Scope;
import net.silthus.chat.conversations.Channel;
import net.silthus.configmapper.ConfigOption;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
@Accessors(fluent = true)
@Scope.Name("local")
public final class LocalScope implements Scope {

    @ConfigOption
    int range = 100;

    @Override
    public Collection<ChatTarget> filterTargets(Channel channel, Message message) {
        Player source = Bukkit.getPlayer(message.getSource().getUniqueId());
        if (source == null) return channel.getTargets();
        return channel.getTargets().stream()
                .filter(target -> isNoPlayerOrInRange(target, source.getLocation()))
                .collect(Collectors.toList());
    }

    private boolean isNoPlayerOrInRange(ChatTarget target, Location source) {
        Player player = Bukkit.getPlayer(target.getUniqueId());
        if (player == null) return true;
        return player.getLocation().distance(source) <= range;
    }
}
