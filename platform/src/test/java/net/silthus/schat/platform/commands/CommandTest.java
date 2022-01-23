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
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.platform.sender.SenderMock;
import net.silthus.schat.policies.AllowJoinChannelStub;
import org.junit.jupiter.api.BeforeEach;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.chatter.ChatterProviderStub.chatterProviderStub;
import static net.silthus.schat.platform.commands.CommandTestUtils.createCommandManager;
import static net.silthus.schat.platform.commands.parser.ChannelArgument.registerChannelArgument;
import static net.silthus.schat.platform.commands.parser.ChatterArgument.registerChatterArgument;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class CommandTest {

    protected CommandManager<Sender> commandManager;
    protected SenderMock sender;
    protected Commands commands;
    protected ChatterProvider chatterProvider;
    protected ChannelRepository channelRepository;

    @BeforeEach
    void setUpBase() {
        commandManager = createCommandManager();
        channelRepository = createInMemoryChannelRepository();

        final Chatter chatter = randomChatter();
        chatterProvider = chatterProviderStub(chatter);
        sender = new SenderMock(chatter.getIdentity());

        commands = new Commands(commandManager);
        registerArgumentTypes();
    }

    @SneakyThrows
    protected Sender cmd(String command) {
        return commandManager.executeCommand(sender, command).get().getCommandContext().getSender();
    }

    protected void cmdFails(String command, Class<? extends Exception> expectedException) {
        try {
            cmd(command);
        } catch (Exception e) {
            assertThat(e).getRootCause().isInstanceOf(expectedException);
        }
    }

    protected void assertLastMessageIs(Component component) {
        sender.assertLastMessageIs(component);
    }

    private void registerArgumentTypes() {
        registerChatterArgument(commandManager, chatterProvider);
        registerChannelArgument(commandManager, channelRepository, new AllowJoinChannelStub());
    }
}
