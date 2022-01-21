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

package net.silthus.schat.channel;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterProvider;
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
    private JoinChannel.Out joinChannelOut = result -> {
    };
    private CanJoinChannel canJoinChannel = (chatter, channel) -> true;

    @Override
    public void joinChannel(@NonNull UUID chatterId, @NonNull String channelId) throws Repository.NotFound, JoinChannel.Error {
        final Chatter chatter = getChatter(chatterId);
        final Channel channel = getChannel(channelId);
        joinChannel(chatter, channel);
    }

    @NotNull
    private Chatter getChatter(@NotNull UUID chatterId) {
        return chatterProvider().get(chatterId);
    }

    @NotNull
    private Channel getChannel(@NotNull String channelId) {
        return channelRepository().get(channelId);
    }

    protected void joinChannel(Chatter chatter, Channel channel) throws JoinChannel.Error {
        if (canJoinChannel.canJoinChannel(chatter, channel))
            joinChannelAndNotifyPresenter(chatter, channel);
        else
            handleJoinChannelError(chatter, channel);
    }

    private void joinChannelAndNotifyPresenter(Chatter chatter, Channel channel) {
        chatter.join(channel);
        notifyJoinChannelPresenter(chatter, channel);
    }

    private void notifyJoinChannelPresenter(Chatter chatter, Channel channel) {
        joinChannelOut().joinedChannel(new JoinChannel.Result(chatter, channel));
    }

    private void handleJoinChannelError(Chatter chatter, Channel channel) {
        leaveChannel(chatter, channel);
        throw new JoinChannel.AccessDenied();
    }

    private void leaveChannel(Chatter chatter, Channel channel) {
        chatter.leave(channel);
    }

    @Override
    public void setActiveChannel(@NonNull UUID chatterId, @NonNull String channelId) throws Repository.NotFound, JoinChannel.Error {
        final Chatter chatter = getChatter(chatterId);
        final Channel channel = getChannel(channelId);
        setActiveChannel(chatter, channel);
    }

    protected void setActiveChannel(Chatter chatter, Channel channel) {
        joinChannel(chatter, channel);
        chatter.setActiveChannel(channel);
    }
}
