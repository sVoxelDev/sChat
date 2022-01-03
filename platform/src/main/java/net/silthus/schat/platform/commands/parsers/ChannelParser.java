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

package net.silthus.schat.platform.commands.parsers;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import io.leangen.geantyref.TypeToken;
import java.util.List;
import java.util.Queue;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.repository.ChannelRepository;
import net.silthus.schat.repository.Repository;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import static cloud.commandframework.arguments.parser.ArgumentParseResult.failure;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.success;

public final class ChannelParser<C> implements ArgumentParser<C, Channel> {

    public static <C> void register(CommandManager<C> commandManager, ChannelRepository repository) {
        commandManager.getParserRegistry().registerParserSupplier(TypeToken.get(Channel.class), parserParameters -> new ChannelParser<>(repository));
    }

    /**
     * Variables: {input}.
     */
    public static final @NonNull Caption ARGUMENT_PARSE_FAILURE_UNKNOWN_CHANNEL = Caption.of("argument.parse.failure.unknown-channel");

    private final ChannelRepository repository;

    public ChannelParser(ChannelRepository repository) {
        this.repository = repository;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Channel> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
        final String input = inputQueue.peek();
        if (input == null)
            return failure(new NoInputProvidedException(ChannelParser.class, commandContext));
        return getChannel(commandContext, inputQueue.remove());
    }

    @NotNull
    private ArgumentParseResult<@NonNull Channel> getChannel(@NotNull CommandContext<@NonNull C> commandContext, String input) {
        try {
            return success(repository.get(input));
        } catch (Repository.NotFound e) {
            return failure(
                new UnknonwChannel(ChannelParser.class,
                    commandContext,
                    ARGUMENT_PARSE_FAILURE_UNKNOWN_CHANNEL,
                    CaptionVariable.of("input", input))
            );
        }
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<C> commandContext, @NonNull String input) {
        return repository.keys();
    }

    public static class UnknonwChannel extends ParserException {

        protected UnknonwChannel(@NonNull Class<?> argumentParser, @NonNull CommandContext<?> context, @NonNull Caption errorCaption, @NonNull CaptionVariable... captionVariables) {
            super(argumentParser, context, errorCaption, captionVariables);
        }
    }
}
