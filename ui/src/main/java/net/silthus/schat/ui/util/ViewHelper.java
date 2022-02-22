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
package net.silthus.schat.ui.util;

import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;

public final class ViewHelper {

    @NotNull
    public static Component renderPrivateChannelName(Chatter chatter, Channel channel) {
        return channel.targets().stream()
            .filter(target -> target instanceof Chatter)
            .filter(target -> !target.equals(chatter))
            .findFirst()
            .map(target -> (Chatter) target)
            .map(Identified::displayName)
            .orElse(channel.displayName());
    }

    public static Component renderPartnerName(Chatter viewer, Message message) {
        return message.targets().stream()
            .filter(target -> target instanceof Channel)
            .map(target -> (Channel) target)
            .filter(channel -> channel.is(PRIVATE))
            .map(channel -> renderPrivateChannelName(viewer, channel))
            .findFirst()
            .orElse(Component.empty());
    }

    public static Component renderPrivateMessage(Chatter viewer, Message message) {
        Component name;
        if (message.source().equals(viewer))
            name = translatable("schat.chat.message.you").color(YELLOW);
        else
            name = renderPartnerName(viewer, message).colorIfAbsent(AQUA);

        return name.append(text(": ", GRAY))
            .append(message.text().colorIfAbsent(GRAY));
    }

    private ViewHelper() {
    }
}
