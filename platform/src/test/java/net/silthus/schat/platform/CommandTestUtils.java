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
import cloud.commandframework.CommandTree;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.internal.CommandRegistrationHandler;
import cloud.commandframework.meta.SimpleCommandMeta;
import java.util.function.Function;
import net.silthus.schat.user.User;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public final class CommandTestUtils {

    /**
     * A permission value which always returns {@code false}.
     */
    public static final String FAILING_PERMISSION = "no";

    private CommandTestUtils() {
    }

    private abstract static class TestCommandSenderCommandManager extends CommandManager<User> {

        protected TestCommandSenderCommandManager(
            final Function<CommandTree<User>, CommandExecutionCoordinator<User>> commandExecutionCoordinator,
            final CommandRegistrationHandler commandRegistrationHandler
        ) {
            super(commandExecutionCoordinator, commandRegistrationHandler);
        }

    }

    public static CommandManager<User> createCommandManager() {
        final CommandManager<User> manager = mock(
            TestCommandSenderCommandManager.class,
            withSettings().useConstructor(
                CommandExecutionCoordinator.simpleCoordinator(),
                CommandRegistrationHandler.nullCommandRegistrationHandler()
            ).defaultAnswer(Mockito.CALLS_REAL_METHODS)
        );

        // We don't care about the actual command meta.
        when(manager.createDefaultCommandMeta()).thenReturn(SimpleCommandMeta.empty());

        // The permission check should always return true, unless "no" is the parameter.
        when(manager.hasPermission(any(), anyString())).thenReturn(true);
        when(manager.hasPermission(any(), eq(FAILING_PERMISSION))).thenReturn(false);

        return manager;
    }

}
