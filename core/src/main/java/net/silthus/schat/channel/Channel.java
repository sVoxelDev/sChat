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
import net.kyori.adventure.text.Component;
import net.silthus.schat.command.Check;
import net.silthus.schat.command.Command;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.pointer.Configured;
import net.silthus.schat.pointer.Pointer;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.repository.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public sealed interface Channel extends Entity<String>, Configured.Modifiable<Channel>, Comparable<Channel>, MessageTarget permits ChannelImpl {

    Pointer<String> KEY = Pointer.pointer(String.class, "key");
    Setting<Component> DISPLAY_NAME = Setting.setting(Component.class, "name", Component.empty());

    /**
     * The priority of the channel may determine the position in the view.
     *
     * <p>The lower the priority value the higher the priority of the channel.</p>
     *
     * <p>Default: {@code 100}.</p>
     */
    Setting<Integer> PRIORITY = Setting.setting(Integer.class, "priority", 100);
    /**
     * A protected channel may restrict access to it, by running checks, such as a {@link #JOIN_PERMISSION} check.
     *
     * <p>Default: {@code false}</p>
     */
    Setting<Boolean> PROTECTED = Setting.setting(Boolean.class, "protected", false);
    /**
     * Sets the permission that is required to join the channel, if the channel is protected.
     *
     * <p>Default: {@code 'schat.admin.channel.join'}</p>
     */
    Setting<String> JOIN_PERMISSION = Setting.setting(String.class, "join_permission", "schat.channel.default.join");
    /**
     * Sets the channel as global, relaying messages to all servers in the network.
     *
     * <p>Default: {@code true}</p>
     */
    Setting<Boolean> GLOBAL = Setting.setting(Boolean.class, "global", true);

    static @NotNull Channel createChannel(String key) {
        return channel(key).create();
    }

    static @NotNull Builder channel(String key) {
        return new ChannelImpl.Builder(key);
    }

    @NotNull String getKey();

    default @NotNull Component getDisplayName() {
        return get(DISPLAY_NAME);
    }

    @NotNull @Unmodifiable List<MessageTarget> getTargets();

    void addTarget(MessageTarget target);

    void removeTarget(MessageTarget target);

    interface Builder extends Configured.Builder<Builder> {

        Builder name(Component displayName);

        <C extends Command> Builder check(Check.Type<C> check);

        Channel create();
    }

    final class InvalidKey extends RuntimeException {
    }
}
