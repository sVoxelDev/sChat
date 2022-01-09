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

import java.util.Optional;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.message.Message;
import net.silthus.schat.policies.ChannelPolicies;
import net.silthus.schat.user.User;

import static net.silthus.schat.message.Message.message;

public class Ui {

    private final ChannelPolicies channelPolicies;

    public Ui(@NonNull ChannelPolicies channelPolicies) {
        this.channelPolicies = channelPolicies;
    }

    public final void joinChannel(@NonNull User user, @NonNull Channel channel) throws JoinChannelError {
        if (!channelPolicies.canJoinChannel(user, channel)) {
            throw new JoinChannelError();
        }
        channel.addTarget(user);
        user.addChannel(channel);
    }

    public final void setActiveChannel(@NonNull User user, @NonNull Channel channel) {
        joinChannel(user, channel);
        user.setActiveChannel(channel);
    }

    public final Message chat(@NonNull User user, @NonNull Component text) throws NoActiveChannel {
        final Optional<Channel> channel = user.getActiveChannel();
        if (channel.isEmpty())
            throw new NoActiveChannel();
        return message(text).source(user).to(channel.get()).type(Message.Type.CHAT).send();
    }

    public static final class JoinChannelError extends RuntimeException {
    }

    public static final class NoActiveChannel extends RuntimeException {
    }
}
