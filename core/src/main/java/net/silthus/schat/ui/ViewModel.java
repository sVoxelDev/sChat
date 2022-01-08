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

package net.silthus.schat.ui;

import java.util.List;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.message.Message;
import net.silthus.schat.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class ViewModel {

    private final User user;

    public ViewModel(User user) {
        this.user = user;
    }

    public @NotNull @Unmodifiable List<Message> getMessages() {
        return user.getMessages().stream()
            .sorted()
            .toList();
    }

    public @NotNull @Unmodifiable List<Channel> getChannels() {
        return user.getChannels().stream()
            .sorted()
            .toList();
    }
}
