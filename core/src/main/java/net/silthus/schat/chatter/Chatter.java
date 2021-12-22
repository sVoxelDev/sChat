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

package net.silthus.schat.chatter;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.format.Formatted;
import net.silthus.schat.format.Formatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Messages;
import net.silthus.schat.message.messenger.Messenger;
import net.silthus.schat.view.Viewer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static net.silthus.schat.message.Message.message;

public class Chatter implements MessageTarget, Formatted {

    private final Formatter<Chatter> formatter = new ChatterFormatter();

    private final List<Channel> channels = new ArrayList<>();
    private final Messenger<Chatter> messenger;

    @Getter
    @Setter
    private Component displayName;
    private Channel activeChannel;

    public Chatter(final @NonNull Viewer viewer) {
        this.messenger = Messenger.messenger(Chatter.class, (target, messenger1, message) -> viewer.updateView(target));
    }

    public Chatter() {
        this(chatter -> {});
    }

    public @NotNull @Unmodifiable Messages getMessages() {
        return messenger.getMessages();
    }

    public boolean isActiveChannel(final Channel channel) {
        return activeChannel != null && activeChannel.equals(channel);
    }

    public void setActiveChannel(final Channel channel) {
        join(channel);
        this.activeChannel = channel;
    }

    public void clearActiveChannel() {
        this.activeChannel = null;
    }

    public void join(final Channel channel) {
        channel.addTarget(this);
        channels.add(channel);
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public Message chat(final String text) {
        validateChannel();
        final Message message = createMessage(text);
        sendToActiveChannel(message);
        return message;
    }

    @Override
    public void sendMessage(final Message message) {
        messenger.sendMessage(this, message);
    }

    private void validateChannel() {
        if (activeChannel == null)
            throw new NoActiveChannel();
    }

    private @NotNull Message createMessage(final String text) {
        return message(this, text);
    }

    private void sendToActiveChannel(final Message message) {
        if (activeChannel != null)
            activeChannel.sendMessage(message);
    }

    public Component formatted() {
        return formatter.format(this);
    }

    public static class NoActiveChannel extends RuntimeException {
    }

    private static class ChatterFormatter implements Formatter<Chatter> {

        private static final @NotNull TextComponent DIVIDER = text(" | ");

        @Override
        public Component format(final Chatter chatter) {
            return messages(chatter).append(channels(chatter));
        }

        private Component messages(final Chatter chatter) {
            return chatter.getMessages().formatted();
        }

        @NotNull
        private TextComponent channels(Chatter chatter) {
            final List<Channel> channels = chatter.getChannels();
            if (channels.isEmpty())
                return empty();

            final TextComponent.Builder builder = text().append(text("| "));
            for (final Channel channel : channels) {
                builder.append(channel(chatter, channel)).append(DIVIDER);
            }

            return builder.build();
        }

        @NotNull
        private Component channel(final Chatter chatter, final Channel channel) {
            return chatter.isActiveChannel(channel) ? channel.formatted().decorate(UNDERLINED) : channel.formatted();
        }

        @NotNull
        private TextComponent empty() {
            return text().append(text("No joined channels!")).build();
        }

    }
}
