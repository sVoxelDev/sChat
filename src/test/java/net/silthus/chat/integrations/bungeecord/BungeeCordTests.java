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
import com.google.common.io.ByteStreams;
import net.silthus.chat.*;
import net.silthus.chat.conversations.AbstractConversation;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.ConversationManager;
import net.silthus.chat.identities.AbstractChatTarget;
import net.silthus.chat.identities.ChatterManager;
import net.silthus.chat.scopes.GlobalScope;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static net.silthus.chat.Constants.BungeeCord.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SuppressWarnings("UnstableApiUsage")
public class BungeeCordTests extends TestBase {

    private BungeeCord bungeecord;
    private ArgumentCaptor<byte[]> captor;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        bungeecord = plugin.getBungeecord();
        captor = ArgumentCaptor.forClass(byte[].class);
    }

    @Test
    void sendGlobalChatMessage() {
        assertThat(bungeecord).isInstanceOf(PluginMessageListener.class);
        Chatter chatter = ChatTarget.player(server.addPlayer());

        Message message = Message.message("test").to(chatter).build();
        bungeecord.sendMessage(message);

        assertReceivedBungeeMessage(SEND_MESSAGE);
        assertThat(chatter.getLastReceivedMessage()).isEqualTo(message);
    }

    @Test
    void synchronizeChatter() {
        ChatterManager chatterManager = plugin.getChatterManager();

        PlayerMock player = new PlayerMock(server, "test");
        Chatter chatter = Chatter.player(player);
        chatterManager.removeChatter(chatter);
        assertThat(chatterManager.getChatter(chatter.getUniqueId())).isNull();

        bungeecord.sendChatter(chatter);

        assertReceivedBungeeMessage(SEND_CHATTER);
        assertThat(chatterManager.getChatter(chatter.getUniqueId()))
                .isNotNull().isEqualTo(chatter);
    }

    private void assertReceivedBungeeMessage(String subChannel) {
        verify(bungeecord, atLeastOnce()).onPluginMessageReceived(eq(BUNGEECORD_CHANNEL), any(), captor.capture());
        final boolean anyMatch = captor.getAllValues().stream()
                .map(bytes -> ByteStreams.newDataInput(bytes).readUTF())
                .anyMatch(s -> s.equals(subChannel));
        assertThat(anyMatch).isTrue();
    }

    @Test
    void synchronizeConversation() {
        ConversationManager conversationManager = plugin.getConversationManager();

        Message message = Chatter.player(server.addPlayer()).message("Hi").to(Chatter.player(server.addPlayer())).build();
        Conversation conversation = message.getConversation();
        assertThat(conversationManager.getConversations()).contains(conversation);

        conversationManager.remove(conversation);
        bungeecord.sendConversation(conversation);
        assertThat(conversationManager.getConversations()).contains(conversation);
    }

    @Test
    void sendGlobalPrivateChatMessage() {
        Chatter offlinePlayer = Chatter.player(server.addPlayer());
        server.setPlayers(0);
        Chatter chatter = Chatter.player(server.addPlayer());

        Message message = chatter.message("Hi").to(offlinePlayer).send();

        assertReceivedBungeeMessage(SEND_MESSAGE);
        assertThat(offlinePlayer.getLastReceivedMessage()).isEqualTo(message);
    }

    @Test
    void sendMessageToChannel_keepsFormat() {
        Chatter source = Chatter.player(server.addPlayer());
        Channel channel = createChannel("test", config -> config.scope(new GlobalScope()));
        plugin.getChannelRegistry().remove(channel);
        channel.addTarget(bungeecord);

        Message message = source.message("Hi").to(channel).send();
        assertReceivedBungeeMessage(SEND_MESSAGE);

        assertThat(plugin.getChannelRegistry().contains("test")).isTrue();
        assertThat(plugin.getChannelRegistry().get("test"))
                .isNotNull()
                .extracting(
                        AbstractConversation::getFormat,
                        AbstractChatTarget::getLastReceivedMessage
                ).contains(
                        channel.getFormat(),
                        message
                );
    }

    @Test
    void deleteMessage_removesMessageOnAllServers() {
        final Channel channel = createChannel(config -> config.scope(Scopes.global()));
        plugin.getChannelRegistry().remove(channel);
        channel.addTarget(bungeecord);

        final Message message = Chatter.player(server.addPlayer()).message("test").to(channel).send();
        message.delete();

        assertReceivedBungeeMessage(DELETE_MESSAGE);
        assertThat(plugin.getChannelRegistry().get(channel.getName()))
                .isNotNull().extracting(AbstractChatTarget::getReceivedMessages)
                .asList().doesNotContain(message);
    }
}
