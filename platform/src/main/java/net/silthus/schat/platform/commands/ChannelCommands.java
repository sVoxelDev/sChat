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
package net.silthus.schat.platform.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.ProxiedBy;
import cloud.commandframework.annotations.specifier.Greedy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.platform.config.Config;
import net.silthus.schat.platform.sender.Sender;

import static net.silthus.schat.commands.LeaveChannelCommand.leaveChannel;
import static net.silthus.schat.commands.SetActiveChannelCommand.setActiveChannel;
import static net.silthus.schat.platform.locale.Messages.JOINED_CHANNEL;
import static net.silthus.schat.platform.locale.Messages.JOIN_CHANNEL_ERROR;
import static net.silthus.schat.platform.locale.Messages.LEAVE_CHANNEL_ERROR;
import static net.silthus.schat.platform.locale.Messages.LEFT_CHANNEL;

@Accessors(fluent = true)
public final class ChannelCommands implements Command {

    @Getter(AccessLevel.PRIVATE)
    private final Config config;

    public ChannelCommands(Config config) {
        this.config = config;
    }

    @Override
    public void register(CommandManager<Sender> commandManager, AnnotationParser<Sender> parser) {
        parser.parse(this);
    }

    @ProxiedBy("ch")
    @CommandMethod("channel join <channel>")
    @CommandPermission("schat.player.channel.join")
    void setActiveChannelCmd(Sender sender, Chatter chatter, @Argument("channel") Channel channel) {
        try {
            if (chatter.isActiveChannel(channel))
                return;
            if (setActiveChannel(chatter, channel).raiseError().wasSuccessful()) {
                JOINED_CHANNEL.send(sender, channel);
            }
        } catch (Throwable e) {
            JOIN_CHANNEL_ERROR.send(sender, channel);
        }
    }

    @ProxiedBy("leave")
    @CommandMethod("channel leave <channel>")
    @CommandPermission("schat.player.channel.leave")
    void leaveChannelCmd(Sender sender, Chatter chatter, @Argument("channel") Channel channel) {
        if (!chatter.isJoined(channel))
            return;
        if (leaveChannel(chatter, channel).wasSuccessful()) {
            LEFT_CHANNEL.actionBar(sender, channel);
        } else {
            LEAVE_CHANNEL_ERROR.send(sender, channel);
        }
    }

    @ProxiedBy("ch")
    @CommandMethod("channel message <channel> <message>")
    @CommandPermission("schat.player.channel.quickmessage")
    public void sendQuickMessage(Chatter source, @Argument("channel") Channel channel, @Argument("message") @Greedy String message) {
        source.message(message).to(channel).send();
    }
}
