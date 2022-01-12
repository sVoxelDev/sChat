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
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.policies.ChannelPolicies;
import org.junit.jupiter.api.BeforeEach;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.platform.SenderMock.randomSender;
import static net.silthus.schat.platform.commands.CommandTestUtils.createCommandManager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class CommandTest {

    protected final ChannelRepository channelRepository = createInMemoryChannelRepository();
    protected CommandManager<Sender> commandManager;
    protected ChannelPolicies policies;
    protected Sender commandSender;

    @BeforeEach
    void setUpBase() {
        commandManager = createCommandManager();
        policies = mock(ChannelPolicies.class);
        when(policies.canJoinChannel(any(), any())).thenReturn(true);
        new Commands(commandManager, channelRepository, policies).register();
        commandSender = randomSender();
    }

    @SneakyThrows
    protected Sender cmd(String command) {
        return commandManager.executeCommand(commandSender, command).get().getCommandContext().getSender();
    }

    protected void cmdFails(String command, Class<? extends Exception> expectedException) {
        try {
            cmd(command);
        } catch (Exception e) {
            assertThat(e).getRootCause().isInstanceOf(expectedException);
        }
    }
}
