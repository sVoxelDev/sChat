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

package net.silthus.chat.integrations.bungeecord;

import net.silthus.chat.ChatTarget;
import net.silthus.chat.Constants;
import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import net.silthus.chat.identities.Chatter;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class BungeecordIntegrationTests extends TestBase {

    private BungeecordIntegration bungeecord;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        bungeecord = plugin.getBungeecord();
    }

    @Test
    void sendGlobalChatMessage() {
        assertThat(bungeecord).isInstanceOf(PluginMessageListener.class);
        Chatter chatter = ChatTarget.player(server.addPlayer());

        Message message = Message.message("test").to(chatter).build();
        bungeecord.sendGlobalChatMessage(message);

        verify(bungeecord).onPluginMessageReceived(eq(Constants.BUNGEECORD_CHANNEL), any(), any());
        assertThat(chatter.getLastReceivedMessage()).isEqualTo(message);
    }
}
