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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.command.commands.JoinChannelCommand;
import net.silthus.schat.command.commands.SetActiveChannelCommand;
import net.silthus.schat.policies.CanJoinChannel;
import net.silthus.schat.repository.Repository;
import net.silthus.schat.usecases.JoinChannel;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter(AccessLevel.PROTECTED)
@Accessors(fluent = true)
public class ChannelInteractorImpl implements ChannelInteractor {

    private ChatterProvider chatterProvider;
    private ChannelRepository channelRepository;
    private JoinChannel.Out joinChannelOut = result -> {};
    private CanJoinChannel canJoinChannel = (chatter, channel) -> true;

    @Override
    public void joinChannel(@NonNull UUID chatterId, @NonNull String channelId) throws Repository.NotFound, JoinChannel.Error {
        final Chatter chatter = getChatter(chatterId);
        final Channel channel = getChannel(channelId);
        JoinChannelCommand.joinChannel(chatter, channel)
            .out(joinChannelOut())
            .check(canJoinChannel())
            .create()
            .execute();
    }

    @NotNull
    private Chatter getChatter(@NotNull UUID chatterId) {
        return chatterProvider().get(chatterId);
    }

    @NotNull
    private Channel getChannel(@NotNull String channelId) {
        return channelRepository().get(channelId);
    }

    @Override
    public void setActiveChannel(@NonNull UUID chatterId, @NonNull String channelId) throws Repository.NotFound, JoinChannel.Error {
        final Chatter chatter = getChatter(chatterId);
        final Channel channel = getChannel(channelId);
        final SetActiveChannelCommand command = SetActiveChannelCommand.setActiveChannel(chatter, channel)
            .joinChannelCmd(builder -> builder.out(joinChannelOut()).check(canJoinChannel()))
            .create();
        command.execute();
    }

}
