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

package net.silthus.chat.identities;

import be.seeseemelk.mockbukkit.entity.SimpleMonsterMock;
import net.silthus.chat.Chatter;
import net.silthus.chat.Identity;
import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import net.silthus.chat.conversations.Channel;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CommandSendingChatterTest extends TestBase {

    private ArgumentCaptor<String> captor;
    private CommandSender sender;
    private Chatter chatter;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        captor = ArgumentCaptor.forClass(String.class);
        sender = mock(CommandSender.class);
        when(sender.getName()).thenReturn("Mock");
        chatter = Chatter.commandSender(sender);
    }

    @Test
    void commandSender_createsSender() {
        final SimpleMonsterMock sender = new SimpleMonsterMock(server);
        final Chatter chatter = Chatter.commandSender(sender);
        assertThat(chatter).isNotNull()
                .isInstanceOf(CommandSendingChatter.class)
                .extracting(
                        Identity::getUniqueId,
                        Identity::getName
                ).contains(
                        sender.getUniqueId(),
                        sender.getName()
                );
    }

    @Test
    void sendMessage_printsLineByLine() {
        chatter.sendMessage("Hi");
        chatter.sendMessage("Test");

        verify(sender, times(2)).sendMessage(captor.capture());
        assertThat(captor.getAllValues())
                .contains(
                        "Hi",
                        "Test"
                );
    }

    @Test
    void sendMessage_toChannel() {
        final Channel channel = createChannel("test");
        chatter.setActiveConversation(channel);

        final Message message = chatter.message("hi").to(channel).send();
        assertThat(channel.getLastReceivedMessage()).isEqualTo(message);
        assertReceivedMessage("[test]Mock: hi");
    }

    private void assertReceivedMessage(String message) {
        verify(sender).sendMessage(captor.capture());
        assertThat(cleaned(captor.getValue())).isEqualTo(message);
    }
}