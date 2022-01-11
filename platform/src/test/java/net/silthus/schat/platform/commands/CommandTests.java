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
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.execution.CommandResult;
import cloud.commandframework.meta.CommandMeta;
import io.leangen.geantyref.TypeToken;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.platform.commands.parser.ChannelParser;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.ChatterMock.randomChatter;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.platform.commands.CommandTestUtils.createCommandManager;

class CommandTests {

    private final ChannelRepository channelRepository = createInMemoryChannelRepository();

    @Test
    void name() {
        final CommandManager<Chatter> commandManager = createCommandManager();
        final AnnotationParser<Chatter> parser = new AnnotationParser<>(
            commandManager,
            Chatter.class,
            p -> CommandMeta.simple()
                .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                .build()
        );
        commandManager.getParserRegistry().registerParserSupplier(TypeToken.get(Channel.class), parserParameters -> new ChannelParser(channelRepository));
        parser.parse(new ChannelCommands());

        channelRepository.add(Channel.createChannel("global"));
        final CommandResult<Chatter> result = commandManager.executeCommand(randomChatter(), "ch global").join();
    }
}
