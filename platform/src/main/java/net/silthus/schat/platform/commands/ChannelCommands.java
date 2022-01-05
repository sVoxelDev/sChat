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
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.ProxiedBy;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.ui.ViewProvider;

public class ChannelCommands {

    private final ViewProvider viewProvider;

    public ChannelCommands(ViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    @ProxiedBy("ch")
    @CommandMethod(value = "channel join <channel>")
    public void joinChannel(Chatter chatter, @Argument Channel channel) {
        chatter.join(channel);
        chatter.setActiveChannel(channel);
        viewProvider.getView(chatter).update();
    }
}
