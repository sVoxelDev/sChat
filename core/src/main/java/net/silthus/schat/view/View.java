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

package net.silthus.schat.view;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.pointer.Configured;
import net.silthus.schat.pointer.Setting;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static net.silthus.schat.pointer.Setting.setting;

public interface View extends Configured.Modifiable<View> {

    Key VIEW_MARKER_KEY = Key.key("schat", "view");
    Component VIEW_MARKER = net.kyori.adventure.text.Component.storageNBT(VIEW_MARKER_KEY.asString(), VIEW_MARKER_KEY);

    Setting<Integer> VIEW_HEIGHT = setting(Integer.class, "format.height", 100); // minecraft chat box height in lines
    Setting<Channel.Format> ACTIVE_CHANNEL_FORMAT = setting(Channel.Format.class, "format.active_channel", c -> c.getDisplayName().decorate(UNDERLINED).colorIfAbsent(GREEN));
    Setting<Channel.Format> INACTIVE_CHANNEL_FORMAT = setting(Channel.Format.class, "format.inactive_channel", c -> c.getDisplayName().colorIfAbsent(GRAY));
    Setting<JoinConfiguration> CHANNEL_JOIN_CONFIG = setting(JoinConfiguration.class, "format.channel_join_config", JoinConfiguration.builder()
        .prefix(text("| "))
        .separator(text(" | "))
        .suffix(text(" |"))
        .build());
    Setting<Format.Component> MESSAGE_SOURCE_FORMAT = setting(Format.Component.class, "format.message_source", name -> name.append(text(": ")));

    static View empty() {
        return net.kyori.adventure.text.Component::empty;
    }

    Component render();

    default boolean isRenderedView(Component render) {
        return render.contains(VIEW_MARKER) || render.children().contains(VIEW_MARKER);
    }
}
