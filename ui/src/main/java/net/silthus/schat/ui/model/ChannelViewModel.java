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

package net.silthus.schat.ui.model;

import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.pointer.Configured;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.ui.Click;

import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.silthus.schat.pointer.Setting.setting;

public class ChannelViewModel implements Configured.Modifiable<ChatterViewModel> {
    public static final Setting<Boolean> CHANNEL_IS_ACTIVE = setting(Boolean.class, "active", false);
    public static final Setting<TextDecoration> ACTIVE_CHANNEL_DECORATION = setting(TextDecoration.class, "active_channel.decoration", TextDecoration.UNDERLINED);
    public static final Setting<TextColor> ACTIVE_CHANNEL_COLOR = setting(TextColor.class, "active_channel.color", NamedTextColor.GREEN);
    public static final Setting<Click.Channel> ACTIVE_CHANNEL_CLICK = setting(Click.Channel.class, "active_channel.click", c -> clickEvent(ClickEvent.Action.RUN_COMMAND, "/channel join " + c.getKey()));

    private final Channel channel;
    @Getter
    private final Settings settings;

    public ChannelViewModel(@NonNull Channel channel) {
        this.channel = channel;
        this.settings = Settings.settings().create();
    }

    public Component render() {
        final Component name = channel.getDisplayName().clickEvent(get(ACTIVE_CHANNEL_CLICK).onClick(channel));
        if (get(CHANNEL_IS_ACTIVE))
            return name.colorIfAbsent(get(ACTIVE_CHANNEL_COLOR))
                .decorate(get(ACTIVE_CHANNEL_DECORATION));
        else
            return name;
    }
}
