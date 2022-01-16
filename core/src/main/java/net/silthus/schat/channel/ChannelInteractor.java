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

import java.util.UUID;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.policies.CanJoinChannel;
import net.silthus.schat.repository.Repository;
import net.silthus.schat.usecases.JoinChannel;
import org.jetbrains.annotations.NotNull;

@Setter
@Accessors(fluent = true)
public final class ChannelInteractor {
    private ChatterRepository chatterRepository;
    private ChannelRepository channelRepository;
    private JoinChannel.Presenter joinChannelPresenter;
    private CanJoinChannel canJoinChannel;

    public void joinChannel(@NonNull UUID chatterId, @NonNull String channelId) throws Repository.NotFound, JoinChannel.Error {
        final Chatter chatter = getChatter(chatterId);
        final Channel channel = getChannel(channelId);
        if (canJoinChannel.canJoinChannel(chatter, channel))
            joinChannelAndNotifyPresenter(chatter, channel);
        else
            handleJoinChannelError(chatter, channel);
    }

    @NotNull
    private Chatter getChatter(@NotNull UUID chatterId) {
        return chatterRepository.get(chatterId);
    }

    @NotNull
    private Channel getChannel(@NotNull String channelId) {
        return channelRepository.get(channelId);
    }

    private void joinChannelAndNotifyPresenter(Chatter chatter, Channel channel) {
        joinChannel(chatter, channel);
        notifyJoinChannelPresenter(chatter, channel);
    }

    private void handleJoinChannelError(Chatter chatter, Channel channel) {
        leaveChannel(chatter, channel);
        throw new JoinChannel.AccessDenied();
    }

    private void joinChannel(Chatter chatter, Channel channel) {
        channel.addTarget(chatter);
        chatter.addChannel(channel);
    }

    private void notifyJoinChannelPresenter(Chatter chatter, Channel channel) {
        joinChannelPresenter.joinedChannel(new JoinChannel.Result(chatter.getIdentity(), channel.getKey(), channel.getDisplayName()));
    }

    private void leaveChannel(Chatter chatter, Channel channel) {
        channel.removeTarget(chatter);
        chatter.removeChannel(channel);
    }
}
