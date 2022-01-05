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

package net.silthus.schat.bukkit.protocollib;

import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.bukkit.BukkitPluginTest;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.SenderChatterLookup;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.bukkit.protocollib.ChatPacketListener.MESSAGE_MARKER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatPacketListenerTest extends BukkitPluginTest {

    @Test
    void processMessage_isNotProcessedIfMarkerExists() {
        final TextComponent message = text("Hi").append(MESSAGE_MARKER);
        final SenderChatterLookup chatterLookup = mock(SenderChatterLookup.class);
        final Chatter chatter = mock(Chatter.class);
        when(chatterLookup.getChatter(any())).thenReturn(chatter);
        final ChatPacketListener packetListener = new ChatPacketListener(bootstrap().getLoader(), plugin().getSenderFactory(), chatterLookup);
        final boolean result = packetListener.processMessage(server().addPlayer(), message);
        assertThat(result).isFalse();
        verify(chatter, never()).sendMessage(any());
    }

    @Test
    void processMessage_isProcessed() {
        final TextComponent message = text("Hi");
        final ChatPacketListener packetListener = new ChatPacketListener(bootstrap().getLoader(), plugin().getSenderFactory(), plugin().getChatters());
        final boolean result = packetListener.processMessage(server().addPlayer(), message);
        assertThat(result).isTrue();
    }
}
