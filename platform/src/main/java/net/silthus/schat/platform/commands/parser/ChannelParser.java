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
import net.silthus.schat.chatter.Chatter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import static cloud.commandframework.arguments.parser.ArgumentParseResult.failure;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.success;

public final class ChannelParser implements ArgumentParser<Chatter, Channel> {

    public static final Caption ARGUMENT_PARSE_FAILURE_CHANNEL = Caption.of("argument.parse.failure.channel");

    public static void registerChannelParser(CommandManager<Chatter> commandManager, ChannelRepository repository) {
        registerArgumentParser(commandManager, repository);
        registerCaptions(commandManager);
    }

    private static void registerArgumentParser(CommandManager<Chatter> commandManager, ChannelRepository repository) {
        commandManager.getParserRegistry().registerParserSupplier(TypeToken.get(Channel.class), parserParameters -> new ChannelParser(repository));
    }

    private static void registerCaptions(CommandManager<Chatter> commandManager) {
        if (commandManager.getCaptionRegistry() instanceof FactoryDelegatingCaptionRegistry<Chatter> registry) {
            registry.registerMessageFactory(
                ChannelParser.ARGUMENT_PARSE_FAILURE_CHANNEL,
                (context, key) -> "'{input}' is not a channel."
            );
        }
    }

    private final ChannelRepository repository;

    public ChannelParser(ChannelRepository repository) {
        this.repository = repository;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Channel> parse(@NonNull CommandContext<@NonNull Chatter> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
        try {
            return getChannel(commandContext, validateAndGetInput(commandContext, inputQueue));
        } catch (Exception e) {
            return failure(e);
        }
    }

    @NotNull
    private ArgumentParseResult<@NonNull Channel> getChannel(@NotNull CommandContext<@NonNull Chatter> commandContext, String input) {
        try {
            return success(repository.get(input));
        } catch (ChannelRepository.ChannelNotFound e) {
            throw new ChannelParseException(commandContext, input);
        }
    }

    @NotNull
    private String validateAndGetInput(@NotNull CommandContext<@NonNull Chatter> commandContext, @NotNull Queue<@NonNull String> inputQueue) {
        final String input = inputQueue.peek();
        if (input == null || input.isBlank())
            throw new NoInputProvidedException(ChannelParser.class, commandContext);
        return inputQueue.remove();
    }

    public static final class ChannelParseException extends ParserException {
        public ChannelParseException(@NonNull CommandContext<?> context, String input) {
            super(ChannelParser.class,
                context,
                ARGUMENT_PARSE_FAILURE_CHANNEL,
                CaptionVariable.of("input", input)
            );
        }
    }
}
