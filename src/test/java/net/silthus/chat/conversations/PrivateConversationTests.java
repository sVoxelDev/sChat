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

package net.silthus.chat.conversations;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.*;
import net.silthus.chat.config.PrivateChatConfig;
import net.silthus.chat.identities.AbstractIdentity;
import net.silthus.chat.renderer.TabbedMessageRenderer;
import net.silthus.chat.renderer.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class PrivateConversationTests extends TestBase {

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
        chatter1 = Chatter.player(player1);
        player2 = server.addPlayer();
        chatter2 = Chatter.player(player2);
        conversation = Conversation.privateConversation(chatter1, chatter2);
    }

    @Test
    void create() {
        assertThat(conversation)
                .isNotNull()
                .isInstanceOf(PrivateConversation.class)
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

        assertReceivedMessage(player1, "Player0: hi");
        assertReceivedMessage(player2, "Player0: hi");
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

        Channel channel = Channel.createChannel("test");
        chatter1.setActiveConversation(channel);
        assertThat(chatter1.getActiveConversation()).isEqualTo(channel);

        sendMessage();
        assertThat(chatter1.getActiveConversation()).isEqualTo(channel);
        assertThat(chatter2.getActiveConversation()).isEqualTo(conversation);
    }

    @Test
    void send_reuses_existingDirectConversation() {
        sendMessage();

        Message message = chatter2.message("hi").to(chatter1).send();

        assertThat(message.getConversation())
                .isNotNull()
                .isEqualTo(conversation);
        assertThat(conversation.getReceivedMessages()).hasSize(2);
        assertThat(conversation.getLastReceivedMessage()).isEqualTo(message);
    }

    @Test
    void send_sendsMessageToBungee() {
        Message message = sendMessage();

        verify(plugin.getBungeecord()).sendMessage(message);
    }

    @Test
    void getName_formatsToOtherPlayer() {
        Message message = sendMessage();

        TabbedMessageRenderer renderer = new TabbedMessageRenderer();

        assertThat(toCleanText(renderer.render(new View(chatter1)))).contains("\u2502 \u2718Player1 \u2502");
        assertThat(toCleanText(renderer.render(new View(chatter2)))).contains("\u2502 \u2718Player0 \u2502");
    }

    @Test
    void message_isFormatted_accordingToFormat() {
        final PrivateChatConfig config = PrivateChatConfig.builder().format(Formats.noFormat()).build();
        final PrivateConversation conversation = new PrivateConversation(config, Chatter.player(server.addPlayer()));
        final Message message = Message.message("test").to(conversation).send();
        assertComponents(message.formatted(), text("test"));
    }

    @Test
    void equalsBasedOnTargetAndSource() {
        Conversation secondConversation = Conversation.privateConversation(chatter2, chatter1);
        assertThat(conversation).isEqualTo(secondConversation);
    }

    @Test
    void withConfig_setsValues() {
        final PrivateChatConfig config = PrivateChatConfig.builder()
                .format(Formats.noFormat())
                .global(false)
                .name(text("Foobar"))
                .build();
        final PrivateConversation conversation = new PrivateConversation(config, chatter1, chatter2);
        assertThat(conversation)
                .extracting(
                        AbstractConversation::getFormat,
                        AbstractIdentity::getDisplayName
                ).contains(
                        Formats.noFormat(),
                        text("Foobar")
                );
        assertThat(conversation.getTargets()).doesNotContain(plugin.getBungeecord());
    }

    @Test
    void withGlobal_addsBungeeCord() {
        final PrivateChatConfig config = PrivateChatConfig.builder().global(true).build();
        final PrivateConversation conversation = new PrivateConversation(config);
        assertThat(conversation.getTargets()).contains(plugin.getBungeecord());
    }

    private Message sendMessage() {
        return chatter1.message("hi").to(conversation).send();
    }
}
