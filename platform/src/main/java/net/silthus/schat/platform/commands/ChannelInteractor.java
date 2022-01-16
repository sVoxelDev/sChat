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
import lombok.NonNull;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.policies.Policies;
import net.silthus.schat.usecases.JoinChannel;
import net.silthus.schat.usecases.SetActiveChannel;

import static net.silthus.schat.locale.Messages.JOIN_CHANNEL_ERROR;

public class ChannelInteractor implements JoinChannel, SetActiveChannel {

    protected final Policies policies;

    public ChannelInteractor(Policies policies) {
        this.policies = policies;
    }

    @Override
    public void joinChannel(@NonNull Chatter chatter, @NonNull Channel channel) throws Error {
        if (!policies.canJoinChannel(chatter, channel))
            throw new Error();
        channel.addTarget(chatter);
        chatter.join(channel);
    }

    @Override
    public void setActiveChannel(@NonNull Chatter chatter, @NonNull @Argument("channel") Channel channel) {
        try {
            joinChannel(chatter, channel);
            chatter.setActiveChannel(channel);
        } catch (Error e) {
            JOIN_CHANNEL_ERROR.send(chatter, channel);
        }
    }
}
