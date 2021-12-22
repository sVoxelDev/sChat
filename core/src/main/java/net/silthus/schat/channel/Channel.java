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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
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
    private static final Pattern VALID_CHANNEL_ID = Pattern.compile("^[a-z0-9_-]+$");

    private final Formatter<Channel> formatter = new ChannelFormatter();

    @Getter
    private final String key;
    private final Set<MessageTarget> targets = new HashSet<>();
    private Messenger<Channel> messenger;

    @Getter
    @Setter
    private Component displayName;

    public Channel(final String key) {
        if (isInvalidChannelKey(key))
            throw new InvalidKey();
        this.key = key;
        this.displayName = text(key);
        this.messenger = Messenger.messenger(Channel.class, new DefaultChannelStrategy());
    }

    public Set<MessageTarget> getTargets() {
        return Collections.unmodifiableSet(targets);
    }

    public @NotNull @Unmodifiable Messages getMessages() {
        return messenger.getMessages();
    }

    public void addTarget(final MessageTarget target) {
        this.targets.add(target);
    }

    @Override
    public void sendMessage(final Message message) {
        messenger.sendMessage(this, message);
    }

    @Override
    public @NotNull Component formatted() {
        return formatter.format(this);
    }

    private boolean isInvalidChannelKey(final String key) {
        return VALID_CHANNEL_ID.asMatchPredicate().negate().test(key);
    }

    public void setMessengerStrategy(final Messenger.Strategy<Channel> strategy) {
        this.messenger = Messenger.messenger(Channel.class, strategy);
    }

    private static class ChannelFormatter implements Formatter<Channel> {

        public @NotNull Component format(Channel channel) {
            return channel.getDisplayName()
                .clickEvent(clickEvent(RUN_COMMAND, JOIN_COMMAND + channel.getKey()));
        }
    }

    private static class DefaultChannelStrategy implements Messenger.Strategy<Channel> {

        @Override
        public void processMessage(final Channel target, final Messenger<Channel> messenger, final Message message) {
            target.getTargets().forEach(messageTarget -> messageTarget.sendMessage(message));
        }
    }

    public static class InvalidKey extends RuntimeException {
    }
}
