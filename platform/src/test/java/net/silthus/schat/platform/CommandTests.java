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

package net.silthus.schat.platform;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.execution.CommandResult;
import cloud.commandframework.meta.CommandMeta;
import net.silthus.schat.platform.commands.ChannelCommands;
import net.silthus.schat.user.User;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.UserHelper.randomUser;
import static net.silthus.schat.platform.CommandTestUtils.createCommandManager;

class CommandTests {

    @Test
    void name() {
        final CommandManager<User> commandManager = createCommandManager();
        final AnnotationParser<User> parser = new AnnotationParser<>(
            commandManager,
            User.class,
            p -> CommandMeta.simple()
                .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                .build()
        );
        parser.parse(new ChannelCommands());

        final CommandResult<User> result = commandManager.executeCommand(randomUser(), "ch global").join();
    }
}
