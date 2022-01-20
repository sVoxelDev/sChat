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
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.platform.sender.Sender;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.platform.commands.parser.ChannelArgument.registerChannelParser;
import static net.silthus.schat.platform.commands.parser.ChatterArgument.registerChatterArgument;

public final class Commands {

    private final CommandManager<Sender> commandManager;
    private final AnnotationParser<Sender> annotationParser;

    public Commands(CommandManager<Sender> commandManager, Context context) {
        this.commandManager = commandManager;
        this.annotationParser = createAnnotationParser();
        registerChatterArgument(commandManager, context.chatterProvider());
        registerChannelParser(commandManager, context.channelRepository());
    }

    public void register(Command... commands) {
        for (final Command command : commands) {
            command.register(commandManager, annotationParser);
        }
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

    public record Context(ChatterProvider chatterProvider, ChannelRepository channelRepository) {
    }
}
