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

package net.silthus.schat.platform.plugin;

import net.silthus.schat.SenderMock;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterStore;
import net.silthus.schat.sender.PlayerAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.SenderMock.senderMock;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static net.silthus.schat.identity.Identity.identity;
import static net.silthus.schat.message.Message.message;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class ChatterManagerTests {

    private ChatterManager chatters;
    private SenderMock sender;
    private Chatter chatter;

    @BeforeEach
    void setUp() {
        chatters = new ChatterManager(createInMemoryChatterRepository(), mock(ChatterStore.class), mock(PlayerAdapter.class));
        sender = spy(senderMock(identity("test"), permission -> true));
        chatter = chatters.getChatter(sender);
    }

    @Test
    void create_usesIdentityOfSender() {
        assertThat(chatter.getIdentity()).isSameAs(sender.getIdentity());
    }

    @Test
    void create_twice_reuses_existingChatter() {
        final Chatter chatter2 = chatters.getChatter(sender);
        assertThat(chatter).isSameAs(chatter2);
    }

    @Test
    void sendMessage_usesSender() {
        final Chatter chatter = chatters.getChatter(sender);
        chatter.sendMessage(message("Hi"));
        verify(sender).sendMessage(any());
    }
}
