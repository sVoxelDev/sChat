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

import java.util.function.BiFunction;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Pointered;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.ui.View;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static net.silthus.schat.channel.Channel.DISPLAY_NAME;
import static net.silthus.schat.pointer.Setting.setting;

@FunctionalInterface
public interface Format {
    /**
     * The default decoration of an active channel.
     */
    Function<Component, Component> ACTIVE_CHANNEL_DECORATION = name -> name.colorIfAbsent(GREEN).decorate(UNDERLINED);
    /**
     * The default decoration of an inactive channel.
     */
    BiFunction<Channel, Component, Component> INACTIVE_CHANNEL_DECORATION = (channel, name) ->
        name.colorIfAbsent(GRAY)
            .hoverEvent(translatable("schat.hover.join-channel")
                .args(channel.get(DISPLAY_NAME))
                .color(GRAY)
            ).clickEvent(
                clickEvent(RUN_COMMAND, "/channel join " + channel.key())
            );
    /**
     * The default format of a message.
     */
    Setting<Format> MESSAGE_FORMAT = setting(Format.class, "message_format", (view, msg) ->
        msg.get(Message.SOURCE)
            .filter(Identity.IS_NOT_NIL)
            .map(identity -> identity.displayName().append(text(": ")))
            .orElse(Component.empty())
            .append(msg.getOrDefault(Message.TEXT, Component.empty())));
    /**
     * The default format of an active channel.
     */
    Setting<Format> ACTIVE_CHANNEL_FORMAT = setting(Format.class, "active_channel_format", (view, channel) ->
        ACTIVE_CHANNEL_DECORATION.apply(((Channel) channel).displayName())
    );
    /**
     * The default format of an inactive channel.
     */
    Setting<Format> INACTIVE_CHANNEL_FORMAT = setting(Format.class, "inactive_channel_format", (view, channel) ->
        INACTIVE_CHANNEL_DECORATION.apply((Channel) channel, ((Channel) channel).displayName())
    );

    Component format(View view, Pointered entity);
}
