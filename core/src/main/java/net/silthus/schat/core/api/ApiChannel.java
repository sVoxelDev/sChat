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

package net.silthus.schat.core.api;

import java.util.List;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.core.channel.ChannelEntity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class ApiChannel implements Channel {

    private final ChannelEntity handle;

    public ApiChannel(ChannelEntity handle) {
        this.handle = handle;
    }

    @Override
    public String getKey() {
        return handle.getKey();
    }

    @Override
    public Component getDisplayName() {
        return handle.getDisplayName();
    }

    @Override
    public void setDisplayName(Component displayName) {
        handle.setDisplayName(displayName);
    }

    @Override
    public @NotNull @Unmodifiable List<MessageTarget> getTargets() {
        return handle.getTargets();
    }

    @Override
    public void addTarget(@NonNull MessageTarget target) {
        handle.addTarget(target);
    }

    @Override
    public void removeTarget(@NonNull MessageTarget target) {
        handle.removeTarget(target);
    }

    @Override
    public void sendMessage(@NonNull Message message) {
        handle.sendMessage(message);
    }
}
