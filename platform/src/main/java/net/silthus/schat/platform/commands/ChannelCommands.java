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

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.ProxiedBy;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelInteractor;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.repository.Repository;
import net.silthus.schat.usecases.JoinChannel;

import static net.silthus.schat.platform.locale.Messages.JOIN_CHANNEL_ERROR;

@Setter(AccessLevel.PROTECTED)
@Accessors(fluent = true)
public final class ChannelCommands implements Command {

    private ChannelInteractor interactor;

    public ChannelCommands(ChannelInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void register(CommandManager<Sender> commandManager, AnnotationParser<Sender> parser) {
        parser.parse(this);
    }

    @ProxiedBy("ch")
    @CommandMethod("channel join <channel>")
    void joinChannel(Sender sender, Chatter chatter, @Argument("channel") Channel channel) {
        try {
            interactor.setActiveChannel(chatter.getKey(), channel.getKey());
        } catch (Repository.NotFound | JoinChannel.Error e) {
            JOIN_CHANNEL_ERROR.send(sender, channel);
        }
    }
}
