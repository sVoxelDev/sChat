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

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import java.util.ArrayDeque;
import java.util.List;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.ChatterMock.randomChatter;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.platform.commands.CommandTestUtils.createCommandManager;
import static org.assertj.core.api.Assertions.assertThat;

class ChannelParserTests {

    private ChannelParser parser;
    private ChannelRepository channelRepository;

    @BeforeEach
    void setUp() {
        channelRepository = createInMemoryChannelRepository();
        parser = new ChannelParser(channelRepository);
    }

    private ArgumentParseResult<Channel> parse(String... input) {
        return parser.parse(createContext(), new ArrayDeque<>(List.of(input)));
    }

    @NotNull
    private CommandContext<Chatter> createContext() {
        return new CommandContext<>(randomChatter(), createCommandManager());
    }

    private void assertParseFailure(Class<? extends Throwable> exception, String... input) {
        final ArgumentParseResult<Channel> result = parse(input);
        assertThat(result.getFailure()).isPresent().get().isInstanceOf(exception);
    }

    private void assertParseSuccessful(String input, Channel expected) {
        assertThat(parse(input).getParsedValue()).isPresent().get().isEqualTo(expected);
    }

    @Test
    void given_no_input_then_NoInputProvidedException_is_throw() {
        assertParseFailure(NoInputProvidedException.class);
    }

    @Test
    void given_empty_string_then_NoInputProvidedException_is_thrown() {
        assertParseFailure(NoInputProvidedException.class, "  ");
    }

    @Test
    void given_unknown_channel_then_ChannelNotFound_is_thrown() {
        assertParseFailure(ChannelParser.ChannelParseException.class, "foobar");
    }

    @Nested class given_channel_in_repository {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = createChannel("test");
            channelRepository.add(channel);
        }

        @Test
        void then_parse_returns_channel() {
            assertParseSuccessful("test", channel);
        }
    }
}
