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
import cloud.commandframework.CommandTree;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.internal.CommandRegistrationHandler;
import cloud.commandframework.meta.SimpleCommandMeta;
import java.util.function.Function;
import net.silthus.schat.platform.sender.Sender;
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

    private abstract static class TestCommandSenderCommandManager extends CommandManager<Sender> {

        protected TestCommandSenderCommandManager(
            final Function<CommandTree<Sender>, CommandExecutionCoordinator<Sender>> commandExecutionCoordinator,
            final CommandRegistrationHandler commandRegistrationHandler
        ) {
            super(commandExecutionCoordinator, commandRegistrationHandler);
        }

    }

    public static CommandManager<Sender> createCommandManager() {
        final CommandManager<Sender> manager = mock(
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
