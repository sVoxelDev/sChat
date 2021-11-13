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

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Conversation;
import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import net.silthus.chat.conversations.AbstractConversation;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.ConversationManager;
import net.silthus.chat.identities.Chatter;
import net.silthus.chat.identities.ChatterManager;
import net.silthus.chat.scopes.GlobalScope;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.chat.Constants.BungeeCord.BUNGEECORD_CHANNEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class BungeeCordTests extends TestBase {

    private BungeeCord bungeecord;

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
        bungeecord.sendMessage(message);

        verify(bungeecord, atLeastOnce()).onPluginMessageReceived(eq(BUNGEECORD_CHANNEL), any(), any());
        assertThat(chatter.getLastReceivedMessage()).isEqualTo(message);
    }

    @Test
    void synchronizeChatter() {
        ChatterManager chatterManager = plugin.getChatterManager();

        PlayerMock player = new PlayerMock(server, "test");
        Chatter chatter = Chatter.of(player);
        chatterManager.removeChatter(chatter);
        assertThat(chatterManager.getChatter(chatter.getUniqueId())).isNull();

        bungeecord.sendChatter(chatter);

        verify(bungeecord, atLeastOnce()).onPluginMessageReceived(eq(BUNGEECORD_CHANNEL), any(), any());
        assertThat(chatterManager.getChatter(chatter.getUniqueId()))
                .isNotNull().isEqualTo(chatter);
    }

    @Test
    void synchronizeConversation() {
        ConversationManager conversationManager = plugin.getConversationManager();

        Message message = Chatter.of(server.addPlayer()).message("Hi").to(Chatter.of(server.addPlayer())).build();
        Conversation conversation = message.getConversation();
        assertThat(conversationManager.getConversations()).contains(conversation);

        conversationManager.remove(conversation);
        bungeecord.sendConversation(conversation);
        assertThat(conversationManager.getConversations()).contains(conversation);
    }

    @Test
    void sendGlobalPrivateChatMessage() {
        Chatter offlinePlayer = Chatter.of(server.addPlayer());
        server.setPlayers(0);
        Chatter chatter = Chatter.of(server.addPlayer());

        Message message = chatter.message("Hi").to(offlinePlayer).send();

        verify(plugin.getBungeecord(), atLeastOnce()).sendMessage(message);
        assertThat(offlinePlayer.getLastReceivedMessage()).isEqualTo(message);
    }

    @Test
    void sendMessageToChannel_keepsFormat() {
        Chatter source = Chatter.of(server.addPlayer());
        Channel channel = createChannel("test", config -> config.scope(new GlobalScope()));
        plugin.getChannelRegistry().remove(channel);

        Message message = source.message("Hi").to(channel).send();
        verify(plugin.getBungeecord(), atLeastOnce()).sendMessage(message);

        assertThat(plugin.getChannelRegistry().contains("test")).isTrue();
        assertThat(plugin.getChannelRegistry().getOrCreate("test"))
                .extracting(AbstractConversation::getFormat)
                .isEqualTo(channel.getFormat());
    }
}
