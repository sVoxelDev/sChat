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

import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.platform.commands.ParserTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.platform.commands.parser.ChannelArgument.ARGUMENT_PARSE_FAILURE_CHANNEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ChannelArgumentTests extends ParserTest<Channel> {

    private ChannelRepository channelRepository;

    @BeforeEach
    void setUp() {
        channelRepository = createInMemoryChannelRepository();
        setParser(new ChannelArgument(channelRepository, (chatter, channel) -> true));
    }

    private void registerChannelParser() {
        ChannelArgument.registerChannelArgument(getCommandManager(), mock(ChannelRepository.class), (chatter, channel) -> true);
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
        assertParseFailure(ChannelArgument.ChannelParseException.class, "foobar");
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
    
    @Nested class registerChannelArgument {

        @Test
        void when_ChannelParser_is_registered_then_captions_are_registered() {
            registerChannelParser();
            assertThat(getCaption(ARGUMENT_PARSE_FAILURE_CHANNEL)).isNotNull();
        }

        @Test
        void when_ChannelParser_is_registered_then_parser_is_registered() {
            registerChannelParser();
            assertThat(getParser(Channel.class)).isPresent();
        }
    }
}
