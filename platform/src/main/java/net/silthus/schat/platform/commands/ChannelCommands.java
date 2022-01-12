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

package net.silthus.schat.platform.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.ProxiedBy;
import java.util.Optional;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.AbstractChatter;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.policies.ChannelPolicies;
import net.silthus.schat.usecases.ChatListener;
import net.silthus.schat.usecases.JoinChannel;
import net.silthus.schat.usecases.SetActiveChannel;

import static net.silthus.schat.message.Message.message;

public final class ChannelCommands implements JoinChannel, SetActiveChannel, ChatListener {

    private final ChannelPolicies channelPolicies;

    public ChannelCommands(ChannelPolicies channelPolicies) {
        this.channelPolicies = channelPolicies;
    }

    @ProxiedBy("ch")
    @CommandMethod("channel join <channel>")
    public void joinChannelCmd(@NonNull Chatter chatter, @NonNull @Argument("channel") Channel channel) {
        joinChannel(chatter, channel);
    }

    @Override
    public void joinChannel(@NonNull Chatter chatter, @NonNull Channel channel) throws Error {
        if (!channelPolicies.canJoinChannel(chatter, channel))
            throw new Error();
        channel.addTarget(chatter);
        ((AbstractChatter) chatter).addChannel(channel);
    }

    @Override
    public void setActiveChannel(@NonNull Chatter chatter, @NonNull Channel channel) {
        joinChannel(chatter, channel);
        ((AbstractChatter) chatter).setActiveChannel(channel);
    }

    @Override
    public Message onChat(@NonNull Chatter chatter, @NonNull Component text) throws NoActiveChannel {
        final Optional<Channel> channel = chatter.getActiveChannel();
        if (channel.isEmpty())
            throw new NoActiveChannel();
        return message(text).source(chatter).to(channel.get()).type(Message.Type.CHAT).send();
    }
}
