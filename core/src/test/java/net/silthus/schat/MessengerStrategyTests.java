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

package net.silthus.schat;

import net.silthus.schat.channel.ActiveChannelStrategy;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class MessengerStrategyTests {

    @Test
    void activeChannelStrategy_sendsMessageOnlyToTargetsWithActiveChannel() {
        final ActiveChannelStrategy strategy = new ActiveChannelStrategy();
        final Channel channel = new Channel("test");
        channel.setMessengerStrategy(strategy);
        final Chatter active = spy(new Chatter());
        active.setActiveChannel(channel);
        final Chatter inactive = spy(new Chatter());
        inactive.setActiveChannel(new Channel("foo"));

        channel.addTarget(active);
        channel.addTarget(inactive);

        final Message message = Message.message("Hi");
        channel.sendMessage(message);

        verify(active).sendMessage(message);
        verify(inactive, never()).sendMessage(message);
    }
}
