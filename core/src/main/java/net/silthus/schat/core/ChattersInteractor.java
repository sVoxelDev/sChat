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

package net.silthus.schat.core;

import java.util.UUID;
import lombok.NonNull;

public class ChattersInteractor {

    private final UserAdapter userAdapter;

    public ChattersInteractor(final UserAdapter userAdapter) {
        this.userAdapter = userAdapter;
    }

    public ChatterEntity getPlayerChatter(final UUID playerId) {
        return new ChatterEntity(userAdapter.getUser(playerId));
    }

    public void setActiveChannel(@NonNull ChatterEntity chatter, @NonNull Channel channel) {
        join(chatter, channel);
        chatter.setActiveChannel(channel);
    }

    public void join(@NonNull ChatterEntity chatter, @NonNull Channel channel) {
        chatter.addChannel(channel);
        channel.addTarget(chatter);
    }

    public void leave(@NonNull ChatterEntity chatter, @NonNull Channel channel) {
        chatter.removeChannel(channel);
        channel.removeTarget(chatter);
        if (chatter.isActiveChannel(channel))
            chatter.clearActiveChannel();
    }
}
