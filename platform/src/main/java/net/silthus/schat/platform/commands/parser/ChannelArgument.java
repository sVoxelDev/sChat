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

package net.silthus.schat.platform.commands.parser;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.captions.FactoryDelegatingCaptionRegistry;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import io.leangen.geantyref.TypeToken;
import java.util.Queue;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.repository.Repository;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import static cloud.commandframework.arguments.parser.ArgumentParseResult.failure;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.success;

public final class ChannelArgument implements ArgumentParser<Sender, Channel> {

    public static final Caption ARGUMENT_PARSE_FAILURE_CHANNEL = Caption.of("argument.parse.failure.channel");

    public static void registerChannelParser(CommandManager<Sender> commandManager, ChannelRepository repository) {
        registerArgumentParser(commandManager, repository);
        registerCaptions(commandManager);
    }

    private static void registerArgumentParser(CommandManager<Sender> commandManager, ChannelRepository repository) {
        commandManager.getParserRegistry().registerParserSupplier(TypeToken.get(Channel.class), parserParameters -> new ChannelArgument(repository));
    }

    private static void registerCaptions(CommandManager<Sender> commandManager) {
        if (commandManager.getCaptionRegistry() instanceof FactoryDelegatingCaptionRegistry<Sender> registry) {
            registry.registerMessageFactory(
                ChannelArgument.ARGUMENT_PARSE_FAILURE_CHANNEL,
                (context, key) -> "'{input}' is not a channel."
            );
        }
    }

    private final ChannelRepository repository;

    public ChannelArgument(ChannelRepository repository) {
        this.repository = repository;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Channel> parse(@NonNull CommandContext<@NonNull Sender> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
        try {
            return getChannel(commandContext, validateAndGetInput(commandContext, inputQueue));
        } catch (Exception e) {
            return failure(e);
        }
    }

    @NotNull
    private ArgumentParseResult<@NonNull Channel> getChannel(@NotNull CommandContext<@NonNull Sender> commandContext, String input) {
        try {
            return success(repository.get(input));
        } catch (Repository.NotFound e) {
            throw new ChannelParseException(commandContext, input);
        }
    }

    @NotNull
    private String validateAndGetInput(@NotNull CommandContext<@NonNull Sender> commandContext, @NotNull Queue<@NonNull String> inputQueue) {
        final String input = inputQueue.peek();
        if (input == null || input.isBlank())
            throw new NoInputProvidedException(ChannelArgument.class, commandContext);
        return inputQueue.remove();
    }

    public static final class ChannelParseException extends ParserException {
        public ChannelParseException(@NonNull CommandContext<?> context, String input) {
            super(ChannelArgument.class,
                context,
                ARGUMENT_PARSE_FAILURE_CHANNEL,
                CaptionVariable.of("input", input)
            );
        }
    }
}
