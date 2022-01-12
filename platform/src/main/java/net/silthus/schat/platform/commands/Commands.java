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
import cloud.commandframework.meta.CommandMeta;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.platform.commands.parser.ChannelParser;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.policies.ChannelPolicies;
import org.jetbrains.annotations.NotNull;

public final class Commands {

    private final CommandManager<Sender> commandManager;
    private final ChannelRepository channelRepository;
    private final AnnotationParser<Sender> parser;
    private final ChannelPolicies channelPolicies;

    public Commands(CommandManager<Sender> commandManager, ChannelRepository channelRepository, ChannelPolicies channelPolicies) {
        this.commandManager = commandManager;
        this.channelRepository = channelRepository;
        this.channelPolicies = channelPolicies;
        this.parser = createAnnotationParser();
    }

    public void register() {
        ChannelParser.registerChannelParser(commandManager, channelRepository);
        parser.parse(new ChannelCommands(channelPolicies));
    }

    @NotNull
    private AnnotationParser<Sender> createAnnotationParser() {
        return new AnnotationParser<>(
            this.commandManager,
            Sender.class,
            p -> CommandMeta.simple()
                .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                .build()
        );
    }
}
