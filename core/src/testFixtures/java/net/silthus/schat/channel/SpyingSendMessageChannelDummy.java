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

package net.silthus.schat.channel;

import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@Getter
public class SpyingSendMessageChannelDummy implements Channel {

    private Message lastMessage = null;
    private boolean sendMessageCalled = false;

    public boolean isSendMessageCalledWith(Message message) {
        return sendMessageCalled && message.equals(lastMessage);
    }

    @Override
    public @NotNull String getKey() {
        return null;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return null;
    }

    @Override
    public @NotNull @Unmodifiable List<MessageTarget> getTargets() {
        return null;
    }

    @Override
    public void addTarget(MessageTarget target) {

    }

    @Override
    public void removeTarget(MessageTarget target) {

    }

    @Override
    public int compareTo(@NotNull Channel o) {
        return 0;
    }

    @Override
    public void sendMessage(@NonNull Message message) {
        this.sendMessageCalled = true;
        this.lastMessage = message;
    }
}
