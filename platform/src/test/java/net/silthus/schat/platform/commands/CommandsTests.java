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
import lombok.SneakyThrows;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.platform.commands.parser.ChannelParser;
import net.silthus.schat.policies.ChannelPolicies;
import net.silthus.schat.ui.Ui;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.ChatterMock.randomChatter;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.platform.commands.CommandTestUtils.createCommandManager;
import static org.assertj.core.api.Assertions.assertThat;

class CommandsTests {

    private final ChannelRepository channelRepository = createInMemoryChannelRepository();
    private CommandManager<Chatter> commandManager;

    @BeforeEach
    void setUp() {
        commandManager = createCommandManager();
        new Commands(commandManager, channelRepository, new Ui(new ChannelPolicies())).register();
    }

    @SneakyThrows
    private Chatter execute(String command) {
        return commandManager.executeCommand(randomChatter(), command).get().getCommandContext().getSender();
    }

    private void executeFails(String command, Class<? extends Exception> expectedException) {
        try {
            execute(command);
        } catch (Exception e) {
            assertThat(e).getRootCause().isInstanceOf(expectedException);
        }
    }

    private Channel addChannel(String channel) {
        final Channel c = createChannel(channel);
        channelRepository.add(c);
        return c;
    }

    @Test
    void given_invalid_chanel_join_command_fails() {
        executeFails("channel join test", ChannelParser.ChannelParseException.class);
    }

    @Nested class given_valid_channel {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = addChannel("test");
        }

        @Test
        void then_join_command_succeeds() {
            final Chatter chatter = execute("channel join test");
            assertThat(chatter.getChannels()).contains(channel);
        }
    }
}
