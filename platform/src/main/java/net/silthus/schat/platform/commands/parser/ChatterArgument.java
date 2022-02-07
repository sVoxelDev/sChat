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
import cloud.commandframework.annotations.AnnotationAccessor;
import cloud.commandframework.annotations.injection.ParameterInjector;
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
import java.util.UUID;
import lombok.NonNull;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.platform.sender.Sender;
import org.jetbrains.annotations.NotNull;

import static cloud.commandframework.arguments.parser.ArgumentParseResult.failure;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.success;
import static net.silthus.schat.util.UUIDUtil.isUuid;

public final class ChatterArgument implements ParameterInjector<Sender, Chatter>, ArgumentParser<Sender, Chatter> {

    public static final Caption ARGUMENT_PARSE_FAILURE_CHATTER = Caption.of("argument.parse.failure.chatter");

    public static void registerChatterArgument(CommandManager<Sender> commandManager, ChatterRepository chatterRepository) {
        registerParameterInjector(commandManager, chatterRepository);
        registerArgumentParser(commandManager, chatterRepository);
        registerCaptions(commandManager);
    }

    private static void registerParameterInjector(CommandManager<Sender> commandManager, ChatterRepository chatterRepository) {
        commandManager.parameterInjectorRegistry().registerInjector(Chatter.class, new ChatterArgument(chatterRepository));
    }

    private static void registerArgumentParser(CommandManager<Sender> commandManager, ChatterRepository chatterRepository) {
        commandManager.getParserRegistry().registerParserSupplier(TypeToken.get(Chatter.class), parserParameters -> new ChatterArgument(chatterRepository));
    }

    private static void registerCaptions(CommandManager<Sender> commandManager) {
        if (commandManager.getCaptionRegistry() instanceof FactoryDelegatingCaptionRegistry<Sender> registry) {
            registry.registerMessageFactory(
                ARGUMENT_PARSE_FAILURE_CHATTER,
                (context, key) -> "The player '{input}' does not exist."
            );
        }
    }

    private final ChatterRepository chatterRepository;

    public ChatterArgument(ChatterRepository chatterRepository) {
        this.chatterRepository = chatterRepository;
    }

    @Override
    public Chatter create(@NonNull CommandContext<Sender> context, @NonNull AnnotationAccessor annotationAccessor) {
        return chatterRepository.get(context.getSender().uniqueId());
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Chatter> parse(@NonNull CommandContext<@NonNull Sender> context, @NonNull Queue<@NonNull String> inputQueue) {
        try {
            final String input = validateAndGetInput(context, inputQueue);
            if (isUuid(input)) {
                return parseFromId(input);
            } else {
                return success(null);
            }
        } catch (ParserException e) {
            throw e;
        } catch (Exception e) {
            return failure(new ChatterParseException(context, inputQueue.peek()));
        }
    }

    @NotNull
    private ArgumentParseResult<@NonNull Chatter> parseFromId(String input) {
        return success(chatterRepository.get(UUID.fromString(input)));
    }

    @NotNull
    private String validateAndGetInput(@NotNull CommandContext<@NonNull Sender> commandContext, @NotNull Queue<@NonNull String> inputQueue) {
        final String input = inputQueue.peek();
        if (input == null || input.isBlank())
            throw new NoInputProvidedException(ChannelArgument.class, commandContext);
        return inputQueue.remove();
    }

    public static class ChatterParseException extends ParserException {
        protected ChatterParseException(@NonNull CommandContext<?> context, String input) {
            super(ChatterArgument.class,
                context,
                ARGUMENT_PARSE_FAILURE_CHATTER,
                CaptionVariable.of("input", input)
            );
        }
    }
}
