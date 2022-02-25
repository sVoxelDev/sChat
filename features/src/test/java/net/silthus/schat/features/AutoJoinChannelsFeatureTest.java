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

import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.events.chatter.ChatterJoinedServerEvent;
import net.silthus.schat.events.config.ConfigReloadedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.ConfiguredSetting.set;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.channel.ChannelSettings.AUTO_JOIN;
import static net.silthus.schat.channel.ChannelSettings.PROTECTED;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static org.assertj.core.api.Assertions.assertThat;

class AutoJoinChannelsFeatureTest {

    private final EventBusMock events = EventBusMock.eventBusMock();
    private final ChannelRepository channelRepository = createInMemoryChannelRepository();
    private final ChatterRepository chatterRepository = createInMemoryChatterRepository();

    private final Channel channel = channelWith(AUTO_JOIN, true);
    private final ChatterMock chatter = randomChatter();

    @BeforeEach
    void setUp() {
        channelRepository.add(channel);
        chatterRepository.add(chatter);

        new AutoJoinChannelsFeature(chatterRepository, channelRepository).bind(events);
    }

    @AfterEach
    void tearDown() {
        events.close();
    }

    private void assertAutoJoinedChannel() {
        chatter.assertJoinedChannel(channel);
    }

    private void triggerJoinEvent() {
        events.post(new ChatterJoinedServerEvent(chatter));
    }

    private void triggerReloadEvent() {
        events.post(new ConfigReloadedEvent());
    }

    @Test
    void onJoin_auto_joins_channels() {
        triggerJoinEvent();
        assertAutoJoinedChannel();
    }

    @Test
    void does_not_join_unconfigured_channels() {
        final Channel channel = randomChannel();
        channelRepository.add(channel);
        triggerJoinEvent();
        chatter.assertNotJoinedChannel(channel);
    }

    @Test
    void does_not_join_protected_channels() {
        final Channel channel = channelWith(set(PROTECTED, true), set(AUTO_JOIN, true));
        channelRepository.add(channel);
        triggerJoinEvent();
        chatter.assertNotJoinedChannel(channel);
    }

    @Test
    void given_no_active_channel_sets_joined_channel_as_active() {
        final Channel channel = randomChannel();
        channelRepository.add(channel);
        triggerJoinEvent();
        assertThat(chatter.activeChannel().isPresent()).isTrue();
    }

    @Test
    void onReload_triggers_auto_join() {
        triggerReloadEvent();
        assertAutoJoinedChannel();
    }
}
