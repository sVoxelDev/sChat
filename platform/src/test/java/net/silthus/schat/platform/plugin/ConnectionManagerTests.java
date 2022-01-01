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
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.chatter.Chatters;
import net.silthus.schat.chatter.checks.JoinChannelPermissionCheck;
import net.silthus.schat.platform.SenderMock;
import net.silthus.schat.sender.Sender;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.AUTO_JOIN;
import static net.silthus.schat.channel.Channel.REQUIRES_JOIN_PERMISSION;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.Chatter.JoinChannel.steps;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static net.silthus.schat.platform.ChannelHelper.channelWith;
import static net.silthus.schat.platform.ChannelHelper.createChannelWith;
import static net.silthus.schat.platform.SenderMock.randomSenderMock;
import static org.assertj.core.api.Assertions.assertThat;

class ConnectionManagerTests {

    private ChattersStub chatters;
    private ChannelRepository channels;
    private ConnectionManager connectionManager;
    private SenderMock sender;

    @BeforeEach
    void setUp() {
        chatters = new ChattersStub();
        channels = createInMemoryChannelRepository();
        connectionManager = new ConnectionManager(chatters, channels);
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
    void autoJoins_channels() {
        final Channel channel = add(channelWith(AUTO_JOIN, true));

        connectionManager.join(sender);

        assertThat(chatter().getChannels()).contains(channel);
    }

    @Test
    void autoJoins_joinable_channels_only() {
        final Channel channel = add(channelWith(AUTO_JOIN, true));
        add(createChannelWith(builder -> builder.setting(AUTO_JOIN, true).setting(REQUIRES_JOIN_PERMISSION, true)));

        connectionManager.join(sender);

        assertThat(chatter().getChannels()).containsOnly(channel);
    }

    private static final class ChattersStub implements Chatters {

        @Getter
        private final ChatterRepository repository = createInMemoryChatterRepository();

        @Override
        public Chatter get(Sender sender) {
            final Chatter chatter = Chatter.chatter(sender.getIdentity())
                .joinChannel(steps(new JoinChannelPermissionCheck(sender)))
                .create();
            repository.add(chatter);
            return chatter;
        }
    }
}
