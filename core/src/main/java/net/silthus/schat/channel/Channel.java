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

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.silthus.schat.format.Formatted;
import net.silthus.schat.format.Formatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Messages;
import net.silthus.schat.message.messenger.Messenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;

public class Channel implements MessageTarget, Formatted {

    public static final String JOIN_COMMAND = "/schat channel join ";

    private final Formatter<Channel> formatter = new ChannelFormatter();

    @Getter
    private final String identifier;
    private final Set<MessageTarget> targets = new HashSet<>();
    private final Messenger messenger;

    public Channel(final String identifier) {
        this.identifier = identifier;
        this.messenger = Messenger.messenger((messenger1, message) -> targets.forEach(target -> target.sendMessage(message)));
    }

    public @NotNull @Unmodifiable Messages getMessages() {
        return messenger.getMessages();
    }

    public void addTarget(final MessageTarget target) {
        this.targets.add(target);
    }

    @Override
    public void sendMessage(final Message message) {
        messenger.sendMessage(message);
    }

    @Override
    public @NotNull Component formatted() {
        return formatter.format(this);
    }

    private static class ChannelFormatter implements Formatter<Channel> {

        public @NotNull Component format(Channel channel) {
            return text(channel.getIdentifier())
                .clickEvent(clickEvent(RUN_COMMAND, JOIN_COMMAND + channel.getIdentifier()));
        }
    }
}
