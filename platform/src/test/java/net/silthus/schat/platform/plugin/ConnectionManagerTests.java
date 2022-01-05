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

package net.silthus.schat.platform.plugin;

import lombok.Getter;
import net.silthus.schat.SenderMock;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.repository.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.chatter.ChatterStore;
import net.silthus.schat.chatter.Chatters;
import net.silthus.schat.sender.Sender;
import net.silthus.schat.ui.Renderer;
import net.silthus.schat.ui.View;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.ChannelHelper.ConfiguredSetting.set;
import static net.silthus.schat.ChannelHelper.channelWith;
import static net.silthus.schat.SenderMock.randomSenderMock;
import static net.silthus.schat.channel.Channel.AUTO_JOIN;
import static net.silthus.schat.channel.Channel.REQUIRES_JOIN_PERMISSION;
import static net.silthus.schat.channel.Channels.channels;
import static net.silthus.schat.channel.repository.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ConnectionManagerTests {

    private ChattersStub chatters;
    private ChannelRepository channels;
    private ConnectionManager connectionManager;
    private SenderMock sender;
    private ChatterStore store;

    @BeforeEach
    void setUp() {
        chatters = new ChattersStub();
        channels = createInMemoryChannelRepository();
        store = mock(ChatterStore.class);
        connectionManager = new ConnectionManager(chatters, store, channels().repository(channels).create());
        sender = randomSenderMock();
    }

    @NotNull
    private Channel add(Channel channel) {
        channels.add(channel);
        return channel;
    }

    @NotNull
    private Chatter chatter() {
        return chatters.get(sender.getUniqueId());
    }

    @Test
    void join_autoJoins_channels() {
        final Channel channel = add(channelWith(set(AUTO_JOIN, true), set(REQUIRES_JOIN_PERMISSION, false)));

        connectionManager.join(sender);

        assertThat(chatter().getChannels()).contains(channel);
    }

    @Test
    void join_autoJoins_joinable_channels_only() {
        final Channel channel = add(channelWith(set(AUTO_JOIN, true), set(REQUIRES_JOIN_PERMISSION, false)));
        add(channelWith(set(AUTO_JOIN, true), set(REQUIRES_JOIN_PERMISSION, true)));

        connectionManager.join(sender);

        assertThat(chatter().getChannels()).containsOnly(channel);
    }

    @Test
    void quit_stores_chatter_data() {
        connectionManager.join(sender);
        connectionManager.leave(sender);

        verify(store).save(chatter());
    }

    @Test
    void join_loads_chatter_data() {
        connectionManager.join(sender);

        verify(store).load(chatter());
    }

    private static final class ChattersStub implements Chatters {

        @Getter
        private final ChatterRepository repository = createInMemoryChatterRepository();

        @Override
        public Chatter getChatter(Sender sender) {
            final Chatter chatter = Chatter.createChatter(sender.getIdentity());
            repository.add(chatter);
            return chatter;
        }

        @Override
        public void load(Chatter chatter) {

        }

        @Override
        public void save(Chatter chatter) {

        }

        @Override
        public @NotNull View getView(@NotNull Sender sender) {
            return View.chatterView(sender, getChatter(sender), Renderer.TABBED_CHANNELS);
        }

        @Override
        public @NotNull View getView(@NotNull Chatter chatter) {
            return View.chatterView(mock(Sender.class), chatter, Renderer.TABBED_CHANNELS);
        }
    }
}
