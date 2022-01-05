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

package net.silthus.schat.ui;

import java.util.Comparator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.silthus.schat.channel.Channel;
import org.jetbrains.annotations.NotNull;

@ToString(of = {"channel"})
@EqualsAndHashCode(of = {"channel"})
final class ChannelViewModel implements Comparable<ChannelViewModel> {

    private static final String JOIN_COMMAND = "/channel join ";
    private static final @NotNull PlainTextComponentSerializer PLAIN_TEXT_SERIALIZER = PlainTextComponentSerializer.plainText();

    @Getter(AccessLevel.PROTECTED)
    private final Channel channel;

    ChannelViewModel(Channel channel) {
        this.channel = channel;
    }

    public Component getDisplayName() {
        return channel.getDisplayName().clickEvent(getClickEvent());
    }

    private ClickEvent getClickEvent() {
        return ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, JOIN_COMMAND + channel.getKey());
    }

    private String getName() {
        return PLAIN_TEXT_SERIALIZER.serialize(channel.getDisplayName());
    }

    private boolean isProtected() {
        return channel.get(Channel.REQUIRES_JOIN_PERMISSION);
    }

    @Override
    public int compareTo(@NotNull ChannelViewModel o) {
        return Comparator
            .comparing(ChannelViewModel::isProtected)
            .thenComparing(ChannelViewModel::getName)
            .compare(this, o);
    }

}
