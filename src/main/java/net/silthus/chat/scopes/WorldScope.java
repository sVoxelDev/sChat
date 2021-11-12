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

import net.silthus.chat.ChatTarget;
import net.silthus.chat.Message;
import net.silthus.chat.Scope;
import net.silthus.chat.conversations.Channel;
import net.silthus.configmapper.ConfigOption;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Scope.Name("world")
public final class WorldScope implements Scope {

    @ConfigOption(required = true)
    List<String> worlds;

    @Override
    public Collection<ChatTarget> apply(Channel channel, Message message) {
        return channel.getTargets().stream()
                .filter(this::isNoPlayerOrInWorld)
                .collect(Collectors.toList());
    }

    private boolean isNoPlayerOrInWorld(ChatTarget target) {
        Player player = Bukkit.getPlayer(target.getUniqueId());
        if (player != null)
            return getWorlds().contains(player.getWorld());
        return true;
    }

    @NotNull
    private List<World> getWorlds() {
        return this.worlds.stream()
                .map(Bukkit::getWorld)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
