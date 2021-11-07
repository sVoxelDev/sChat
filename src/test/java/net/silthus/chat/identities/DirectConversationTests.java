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

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.Conversation;
import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.DirectConversation;
import net.silthus.chat.renderer.TabbedMessageRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectConversationTests extends TestBase {

    PlayerMock player1;
    Chatter chatter1;
    PlayerMock player2;
    Chatter chatter2;
    Conversation conversation;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        player1 = server.addPlayer();
        chatter1 = Chatter.of(player1);
        player2 = server.addPlayer();
        chatter2 = Chatter.of(player2);
        conversation = Conversation.direct(chatter1, chatter2);
    }

    @Test
    void create() {
        assertThat(conversation)
                .isNotNull()
                .isInstanceOf(DirectConversation.class)
                .extracting(
                        Conversation::getTargets
                ).asList().contains(
                        chatter1,
                        chatter2
                );
    }

    @Test
    void sendMessage_sendsMessageToBoth() {
        Message message = sendMessage();

        assertThat(conversation.getLastReceivedMessage()).isEqualTo(message);
        assertThat(chatter1.getLastReceivedMessage()).isEqualTo(message);
        assertThat(chatter2.getLastReceivedMessage()).isEqualTo(message);

        assertThat(player1.nextMessage()).isEqualTo("Player0: hi");
        assertThat(player2.nextMessage()).isEqualTo("Player0: hi");
    }

    @Test
    void sendMessage_subscribes_bothToConversation() {
        sendMessage();

        assertThat(chatter1.getConversations()).contains(conversation);
        assertThat(chatter2.getConversations()).contains(conversation);
    }

    @Test
    void sendFirstMessage_setsConversationActive() {
        sendMessage();

        assertThat(chatter1.getActiveConversation()).isEqualTo(conversation);
        assertThat(chatter2.getActiveConversation()).isEqualTo(conversation);
    }

    @Test
    void sendMessage_onlySetsActive_ifNotSubscribed() {
        sendMessage();

        Channel channel = Channel.channel("test");
        chatter1.setActiveConversation(channel);
        assertThat(chatter1.getActiveConversation()).isEqualTo(channel);

        sendMessage();
        assertThat(chatter1.getActiveConversation()).isEqualTo(channel);
        assertThat(chatter2.getActiveConversation()).isEqualTo(conversation);
    }

    @Test
    void getName_formatsToOtherPlayer() {
        Message message = sendMessage();

        TabbedMessageRenderer view = new TabbedMessageRenderer();

        assertThat(toText(view.render(chatter1, message))).contains("Player1&8");
        assertThat(toText(view.render(chatter2, message))).contains("Player0&8");
    }

    private Message sendMessage() {
        return chatter1.message("hi").to(conversation).send();
    }
}
