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
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Pointered;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.ui.View;
import net.silthus.schat.ui.views.tabbed.Tab;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static net.silthus.schat.pointer.Setting.setting;

@FunctionalInterface
public interface Format {
    /**
     * The default decoration of an active channel.
     */
    Function<Component, Component> ACTIVE_TAB_DECORATION = name -> name.colorIfAbsent(GREEN).decorate(UNDERLINED);
    /**
     * The default decoration of an inactive channel.
     */
    BiFunction<Tab, Component, Component> INACTIVE_TAB_DECORATION = (tab, name) ->
        name.colorIfAbsent(GRAY)
            .hoverEvent(translatable("schat.hover.join-channel")
                .args(tab.get(Tab.NAME))
                .color(GRAY)
            ).clickEvent(
                clickEvent(RUN_COMMAND, "/channel join " + tab.get(Tab.KEY).orElse(""))
            );
    /**
     * The default format of a message.
     */
    Setting<Format> MESSAGE_FORMAT = setting(Format.class, "message_format", (view, msg) ->
        msg.get(Message.SOURCE)
            .filter(Identity.IS_NOT_NIL)
            .map(identity -> identity.displayName().colorIfAbsent(YELLOW).append(text(": ", GRAY)))
            .orElse(Component.empty())
            .append(((Message) msg).text().colorIfAbsent(GRAY)));
    /**
     * The default format of a message where the source is the viewer.
     */
    Setting<Format> SELF_MESSAGE_FORMAT = setting(Format.class, "self_message_format", (view, msg) ->
        translatable("schat.chat.message.you").color(YELLOW)
            .append(text(": ", GRAY))
            .append(((Message) msg).text().colorIfAbsent(GRAY)));
    /**
     * The default format of an active tab.
     */
    Setting<Format> ACTIVE_TAB_FORMAT = setting(Format.class, "active_tab_format", (view, tab) ->
        ACTIVE_TAB_DECORATION.apply(tab.getOrDefault(Tab.NAME, text("Unknown")))
    );
    /**
     * The default format of an inactive tab.
     */
    Setting<Format> INACTIVE_TAB_FORMAT = setting(Format.class, "inactive_tab_format", (view, tab) ->
        INACTIVE_TAB_DECORATION.apply((Tab) tab, tab.getOrDefault(Tab.NAME, text("Unknown")))
    );

    Component format(View view, Pointered entity);
}
