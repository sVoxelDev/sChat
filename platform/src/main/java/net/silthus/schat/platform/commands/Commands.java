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
import net.silthus.schat.chatter.Chatter;
import org.jetbrains.annotations.NotNull;

public final class Commands {

    private final CommandManager<Chatter> commandManager;
    private final AnnotationParser<Chatter> parser;

    public Commands(CommandManager<Chatter> commandManager) {
        this.commandManager = commandManager;
        this.parser = createAnnotationParser();
    }

    public void register(Command... commands) {
        for (final Command command : commands) {
            command.register(commandManager, parser);
        }
    }

    @NotNull
    private AnnotationParser<Chatter> createAnnotationParser() {
        return new AnnotationParser<>(
            this.commandManager,
            Chatter.class,
            p -> CommandMeta.simple()
                .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                .build()
        );
    }
}
