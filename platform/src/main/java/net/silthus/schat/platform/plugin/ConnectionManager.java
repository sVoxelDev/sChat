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

import java.util.List;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.repository.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterStore;
import net.silthus.schat.chatter.SenderChatterLookup;
import net.silthus.schat.checks.Check;
import net.silthus.schat.sender.ConnectionListener;
import net.silthus.schat.sender.Sender;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.Channel.AUTO_JOIN;

final class ConnectionManager implements ConnectionListener {

    private final SenderChatterLookup chatterLookup;
    private final ChannelRepository channels;
    private final ChatterStore store;

    ConnectionManager(SenderChatterLookup chatterLookup,
                      ChatterStore store,
                      ChannelRepository channels) {
        this.chatterLookup = chatterLookup;
        this.channels = channels;
        this.store = store;
    }

    @Override
    public void join(Sender sender) {
        final Chatter chatter = chatterLookup.getChatter(sender);
        store.load(chatter);
        autoJoinChannels(chatter);
    }

    @Override
    public void leave(Sender sender) {
        final Chatter chatter = chatterLookup.getChatter(sender);
        store.save(chatter);
    }

    private void autoJoinChannels(Chatter chatter) {
        getAutoJoinChannels().forEach(channel -> autoJoinChannel(chatter, channel));
    }

    private void autoJoinChannel(Chatter chatter, Channel channel) {
        try {
            chatter.join(channel);
        } catch (Check.Error ignored) {
        }
    }

    @NotNull
    private List<Channel> getAutoJoinChannels() {
        return channels.filter(channel -> channel.get(AUTO_JOIN));
    }
}
