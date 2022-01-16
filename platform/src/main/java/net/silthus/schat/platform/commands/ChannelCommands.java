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
import io.leangen.geantyref.TypeToken;
import java.util.Optional;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.platform.commands.parser.ChannelParser;
import net.silthus.schat.policies.Policies;
import net.silthus.schat.usecases.ChatListener;

import static net.silthus.schat.locale.Messages.JOIN_CHANNEL_ERROR;
import static net.silthus.schat.message.Message.message;

public final class ChannelCommands extends ChannelInteractor implements ChatListener, Command {

    private final ChannelRepository channelRepository;

    public ChannelCommands(Policies policies, ChannelRepository channelRepository) {
        super(policies);
        this.channelRepository = channelRepository;
    }

    @Override
    public void register(CommandManager<Chatter> commandManager, AnnotationParser<Chatter> parser) {
        commandManager.getParserRegistry().registerParserSupplier(TypeToken.get(Channel.class), parserParameters -> new ChannelParser(channelRepository));
        parser.parse(this);
    }

    @ProxiedBy("ch")
    @CommandMethod("channel join <channel>")
    public void joinChannelCmd(@NonNull Chatter chatter, @NonNull @Argument("channel") Channel channel) {
        try {
            joinChannel(chatter, channel);
        } catch (Error e) {
            JOIN_CHANNEL_ERROR.send(chatter, channel);
        }
    }

    @Override
    public Message onChat(@NonNull Chatter chatter, @NonNull Component text) throws NoActiveChannel {
        final Optional<Channel> channel = chatter.getActiveChannel();
        if (channel.isEmpty())
            throw new NoActiveChannel();
        return message(text).source(chatter).to(channel.get()).type(Message.Type.CHAT).send();
    }
}
