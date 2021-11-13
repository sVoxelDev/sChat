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

package net.silthus.chat.renderer;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.Message;
import net.silthus.chat.MessageRenderer;
import net.silthus.chat.TestBase;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.identities.Chatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class ViewTest extends TestBase {

    private PlayerMock player;
    private Chatter chatter;
    private View view;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        player = server.addPlayer();
        chatter = Chatter.of(player);
        view = new View(chatter, MessageRenderer.TABBED);
    }

    @Test
    void create_setsRenderer() {
        assertThat(view.renderer())
                .isNotNull().isEqualTo(MessageRenderer.TABBED);
    }

    @Test
    void create_withChatter_usesThePropertiesOfTheChatter() {
        Message message = Message.message("hi").build();
        chatter.sendMessage(message);
        assertThat(view.messages()).contains(message);

        Channel channel = createChannel("test");
        chatter.subscribe(channel);
        assertThat(view.conversations()).contains(channel);

        Channel foobar = createChannel("foobar");
        chatter.setActiveConversation(foobar);
        assertThat(view.activeConversation())
                .isPresent().get()
                .isEqualTo(foobar);
    }

    @Test
    void sendTo_sendsViewToGivenPlayer() {
        chatter.sendMessage(Message.message("testing").build());
        PlayerMock player = server.addPlayer();

        view.sendTo(player);

        assertThat(cleaned(player.nextMessage())).contains("testing");
    }

    @Test
    void lastMessage_onlyShowsMessage_ifSent() {
        Channel test = createChannel("test");
        chatter.subscribe(test);
        chatter.setActiveConversation(createChannel("foo"));

        test.sendMessage("hi");
        assertThat(view.lastMessage()).isEmpty();

        Message message = chatter.sendMessage("hey");
        assertThat(view.lastMessage()).isPresent().get().isEqualTo(message);
    }

    @Test
    void messages_containOnlyUnique() {
        Message message = Message.message("test").build();
        chatter.sendMessage(message);
        Channel channel = createChannel("test");
        chatter.setActiveConversation(channel);
        channel.sendMessage(message);

        assertThat(view.messages()).containsOnlyOnce(message);
    }

    @Test
    void messages_areOrdered() {
        Collection<Message> messages = randomMessages(10);
        messages.forEach(chatter::sendMessage);

        assertThat(view.messages()).isEqualTo(messages.stream().sorted().toList());
    }

    @Test
    void messages_getsSystemAndChannelMessages() throws InterruptedException {
        Channel channel = createChannel("msgtest");
        chatter.setActiveConversation(channel);
        Message systemMessage = Message.message("system").to(chatter).send();
        Thread.sleep(1L);
        Message channelMessage = channel.sendMessage("channel");
//        Message message1 = Message.message("system 1").to(chatter).send();
//        Message message2 = channel.sendMessage("channel 2");
//        Message message3 = Message.message("system 3").to(chatter).send();
//        Message otherMessage = Message.message("test").from(Chatter.of(server.addPlayer())).to(chatter).send();
//        chatter.setActiveConversation(channel);
//        Message message4 = Message.message("channel 4").from(Chatter.of(server.addPlayer())).to(channel).send();

        assertThat(view.messages())
                .hasSize(2)
                .containsExactly(systemMessage, channelMessage);
    }
}