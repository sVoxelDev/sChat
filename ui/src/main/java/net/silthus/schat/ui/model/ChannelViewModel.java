/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.ui.model;

import java.util.function.Supplier;
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

    public ChannelViewModel(@NonNull Channel channel, Supplier<Boolean> isActive) {
        this.channel = channel;
        this.settings = Settings.settings().withDynamic(CHANNEL_IS_ACTIVE, isActive).create();
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
