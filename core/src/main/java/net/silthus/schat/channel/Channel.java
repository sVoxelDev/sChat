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
import net.silthus.schat.repository.Entity;
import net.silthus.schat.settings.Configured;
import net.silthus.schat.settings.Setting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static net.kyori.adventure.text.Component.empty;
import static net.silthus.schat.settings.Setting.setting;

public interface Channel extends MessageTarget, Configured, Entity<String> {

    Setting<Component> DISPLAY_NAME = setting(Component.class, "name", empty());
    Setting<Boolean> REQUIRES_JOIN_PERMISSION = setting(Boolean.class, "requires_join_permission", false)
        .withAlias("protect");
    Setting<String> JOIN_PERMISSION = setting(String.class, "permissions.join", "schat.admin.channel.join");
    Setting<Boolean> AUTO_JOIN = setting(Boolean.class, "auto_join", false);

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

}
