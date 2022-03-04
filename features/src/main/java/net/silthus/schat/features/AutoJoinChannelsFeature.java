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
package net.silthus.schat.features;

import java.util.stream.Stream;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.eventbus.EventListener;
import net.silthus.schat.events.channel.ChannelRegisteredEvent;
import net.silthus.schat.events.chatter.ChatterJoinedServerEvent;
import net.silthus.schat.events.config.ConfigReloadedEvent;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.ChannelSettings.AUTO_JOIN;
import static net.silthus.schat.commands.JoinChannelCommand.joinChannel;
import static net.silthus.schat.commands.SetActiveChannelCommand.setActiveChannel;

public class AutoJoinChannelsFeature implements EventListener {
    private final ChatterRepository chatterRepository;
    private final ChannelRepository channelRepository;

    public AutoJoinChannelsFeature(ChatterRepository chatterRepository, ChannelRepository channelRepository) {
        this.chatterRepository = chatterRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public void bind(EventBus bus) {
        bus.on(ChatterJoinedServerEvent.class, this::onChatterJoin);
        bus.on(ConfigReloadedEvent.class, this::onConfigReload);
        bus.on(ChannelRegisteredEvent.class, this::onRegisteredChannel);
    }

    protected void onChatterJoin(ChatterJoinedServerEvent event) {
        autoJoinChannels(event.chatter());
    }

    protected void onConfigReload(ConfigReloadedEvent event) {
        autoJoinableChannels().forEach(this::autojoinAllChattersToChannel);
    }

    private void autojoinAllChattersToChannel(Channel channel) {
        for (final Chatter chatter : chatterRepository.all())
            autoJoinChannel(chatter, channel);
    }

    protected void onRegisteredChannel(ChannelRegisteredEvent event) {
        if (event.channel().is(AUTO_JOIN))
            autojoinAllChattersToChannel(event.channel());
    }

    private void autoJoinChannels(Chatter chatter) {
        autoJoinableChannels()
            .forEach(channel -> autoJoinChannel(chatter, channel));
    }

    @NotNull
    private Stream<Channel> autoJoinableChannels() {
        return channelRepository.all().stream()
            .filter(channel -> channel.is(AUTO_JOIN));
    }

    private void autoJoinChannel(Chatter chatter, Channel channel) {
        if (chatter.activeChannel().isEmpty())
            setActiveChannel(chatter, channel);
        else
            joinChannel(chatter, channel);
    }
}
