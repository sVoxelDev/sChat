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
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.meta.CommandMeta;
import net.silthus.schat.platform.sender.Sender;
import org.jetbrains.annotations.NotNull;

public final class Commands {

    private final CommandManager<Sender> commandManager;
    private final AnnotationParser<Sender> annotationParser;

    public Commands(CommandManager<Sender> commandManager) {
        this.commandManager = commandManager;
        this.annotationParser = createAnnotationParser();
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

    public void execute(Sender sender, String command) {
        commandManager.executeCommand(sender, command.replaceFirst("/", ""));
    }
}
