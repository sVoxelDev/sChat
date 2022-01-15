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

package net.silthus.schat.platform.config;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import net.silthus.schat.channel.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static java.util.Collections.unmodifiableList;

public class ChannelLoader {

    private final ArrayList<Channel> channels = new ArrayList<>();

    public @NotNull @Unmodifiable List<Channel> getLoadedChannels() {
        return unmodifiableList(channels);
    }

    public void load(@NonNull Channel channel) {
        channels.add(channel);
    }
}
