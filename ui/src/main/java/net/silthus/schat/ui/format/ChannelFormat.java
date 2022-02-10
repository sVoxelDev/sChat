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

package net.silthus.schat.ui.format;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.pointer.Configured;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.ui.Click;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;

@Getter
@Accessors(fluent = true)
public class ChannelFormat implements Format<Channel>, Configured {

    public static final Setting<TextColor> COLOR = Setting.setting(TextColor.class, "color", WHITE);
    public static final Setting<TextDecoration> DECORATION = Setting.setting(TextDecoration.class, "style", null);
    public static final Setting<Click.Channel> ON_CLICK = Setting.setting(Click.Channel.class, "on_click", null);

    private final Chatter chatter;
    private final Settings settings;

    public ChannelFormat(Chatter chatter, Settings settings) {
        this.chatter = chatter;
        this.settings = settings;
    }

    public Component format(Channel channel) {
        TextComponent.Builder builder = text().append(channelName(channel));
        if (get(COLOR) != null)
            builder.color(get(COLOR));
        if (get(DECORATION) != null)
            builder.decorate(get(DECORATION));
        if (get(ON_CLICK) != null)
            builder.clickEvent(get(ON_CLICK).onClick(channel));
        return builder.build();
    }

    @NotNull
    private Component channelName(Channel channel) {
        return channel.is(PRIVATE) ? privateChannelName(channel) : channel.displayName();
    }

    @NotNull
    private Component privateChannelName(Channel channel) {
        return channel.targets().stream()
            .filter(target -> target instanceof Chatter)
            .filter(target -> !target.equals(chatter))
            .findFirst()
            .map(target -> (Chatter) target)
            .map(Identified::displayName)
            .orElse(channel.displayName());
    }
}
