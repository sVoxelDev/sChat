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

import java.util.Set;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Messages;
import net.silthus.schat.message.messenger.Messenger;
import net.silthus.schat.settings.Configured;
import net.silthus.schat.settings.Setting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public interface Channel extends MessageTarget, Configured {

    Setting<Boolean> PUBLIC = Setting.setting(Boolean.class, "public", false);

    static Builder channel(String key) {
        return new ChannelImpl.ChannelImplBuilder(key);
    }

    static Channel createChannel(String key) {
        return channel(key).create();
    }

    @NotNull String getKey();

    @NotNull Component getDisplayName();

    void addTarget(@NonNull MessageTarget target);

    @NotNull @Unmodifiable Set<MessageTarget> getTargets();

    @NotNull @Unmodifiable Messages getMessages();

    interface Builder extends Configured.Builder<Builder> {

        Builder displayName(@NonNull Component displayName);

        Builder messenger(@NonNull Messenger<Channel> messenger);

        Channel create();

    }

    class InvalidKey extends RuntimeException {
    }

    class AccessDenied extends RuntimeException {
    }
}
