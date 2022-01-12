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
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.context.CommandContext;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import net.silthus.schat.platform.sender.Sender;
import org.jetbrains.annotations.NotNull;

import static cloud.commandframework.arguments.parser.ParserParameters.empty;
import static io.leangen.geantyref.TypeToken.get;
import static net.silthus.schat.platform.SenderMock.randomSender;
import static org.assertj.core.api.Assertions.assertThat;

@Getter
@Setter
public abstract class ParserTest<T> {

    private final CommandManager<Sender> commandManager;
    private ArgumentParser<Sender, T> parser;

    protected ParserTest() {
        this.commandManager = CommandTestUtils.createCommandManager();
    }

    @NotNull
    private CommandContext<Sender> createContext() {
        return new CommandContext<>(randomSender(), commandManager);
    }

    protected ArgumentParseResult<T> parse(String... input) {
        return parser.parse(createContext(), new ArrayDeque<>(List.of(input)));
    }

    protected void assertParseFailure(Class<? extends Throwable> exception, String... input) {
        final ArgumentParseResult<T> result = parse(input);
        assertThat(result.getFailure()).isPresent().get().isInstanceOf(exception);
    }

    protected void assertParseSuccessful(String input, T expected) {
        assertThat(parse(input).getParsedValue()).isPresent().get().isEqualTo(expected);
    }

    @NotNull
    protected String getCaption(Caption caption) {
        return commandManager.getCaptionRegistry().getCaption(caption, randomSender());
    }

    @NotNull
    protected Optional<ArgumentParser<Sender, T>> getParser(Class<T> type) {
        return commandManager.getParserRegistry().createParser(get(type), empty());
    }
}
