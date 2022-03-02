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
package net.silthus.schat.ui.views.tabbed;

import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageSource;
import net.silthus.schat.ui.format.Format;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

@Data
@Accessors(fluent = true)
@ConfigSerializable
public class TabFormatConfig {

    @Setting("active_color")
    private TextColor activeColor = NamedTextColor.GREEN;
    @Setting("active_decoration")
    private TextDecoration activeDecoration = TextDecoration.UNDERLINED;

    @Setting("inactive_color")
    private TextColor inactiveColor = NamedTextColor.GRAY;
    @Setting("inactive_decoration")
    private TextDecoration inactiveDecoration = null;

    @Setting("highlight_unread")
    private boolean highlightUnread = true;
    @Setting("unread_color")
    private TextColor unreadColor = NamedTextColor.WHITE;
    @Setting("unread_decoration")
    private TextDecoration unreadDecoration = null;

    @Setting("message_format")
    private Format messageFormat = (view, msg) ->
        msg.get(Message.SOURCE)
            .filter(MessageSource.IS_NOT_NIL)
            .map(identity -> identity.displayName().colorIfAbsent(YELLOW).append(text(": ", GRAY)))
            .orElse(Component.empty())
            .append(((Message) msg).text().colorIfAbsent(GRAY));

    @Setting("self_message_format")
    private Format selfMessageFormat = (view, msg) ->
        translatable("schat.chat.message.you").color(DARK_AQUA)
            .append(text(": ", GRAY))
            .append(((Message) msg).text().colorIfAbsent(GRAY));
}
