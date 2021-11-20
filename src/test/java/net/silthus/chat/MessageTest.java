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

package net.silthus.chat;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.PrivateConversation;
import net.silthus.chat.identities.Console;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class MessageTest extends TestBase {

    @Test
    void ofMessageString() {
        Message message = Message.message("Hello").build();

        assertThat(message.getSource())
                .isNotNull().isEqualTo(ChatSource.nil());
        assertThat(toCleanText(message)).isEqualTo("Hello");
        assertThat(message.getTargets()).isEmpty();
    }

    @Test
    void target_isEmptyByDefault() {

        Message message = Message.message("test").build();
        assertThat(message.getTargets())
                .isEmpty();
    }

    @Test
    void send_sendsMessageToTarget() {

        ChatTarget target = ChatTarget.nil();
        Message message = Message.message("hi").to(target).send();

        assertThat(target.getLastReceivedMessage()).isEqualTo(message);
    }

    @Test
    void toChannel_setsChannel_andTarget() {

        Channel channel = Channel.channel("test");
        Message message = Message.message("hi").to(channel).build();

        assertThat(message.getConversation())
                .isNotNull().isEqualTo(channel);
        assertThat(message.getTargets())
                .contains(channel);
    }

    @Test
    void emptySource_usesDirectMessageFormat() {
        Message message = Message.message("test").build();
        assertThat(toCleanText(message)).isEqualTo("test");
    }

    @Test
    void message_withSource_usesDefaultFormat() {
        Message message = Message.message(ChatSource.named("test"), "Hi there!").build();

        assertThat(toCleanText(message)).isEqualTo("test: Hi there!");
    }

    @Test
    void format_overrides_channelFormat() {
        Message message = ChatSource.named("test")
                .message("Hi")
                .to(Channel.channel("channel"))
                .format(Formats.noFormat())
                .build();
        String text = toCleanText(message);

        assertThat(text).isEqualTo("Hi");
    }

    @Test
    void format_usesChannelFormat_ifNotSet() {
        Message message = ChatSource.named("test")
                .message("Hi")
                .to(Channel.channel("channel"))
                .build();
        String text = toText(message);

        assertThat(text).isEqualTo("&6[&achannel&6]&etest&7: Hi");
    }

    @Test
    void format_noFormat_noChannel_usesDefaultFormat() {
        Message message = ChatSource.named("test")
                .message("Hi")
                .build();
        String text = toCleanText(message);

        assertThat(text).isEqualTo("test: Hi");
    }

    @Test
    void timestamp_isSetOnCreate() {
        Message message = Message.message("test").build();
        assertThat(message.getTimestamp())
                .isNotNull()
                .isCloseTo(Instant.now(), within(100, ChronoUnit.MILLIS));
    }

    @Test
    void copy_createsNewTimestamp() throws InterruptedException {
        Message message = Message.message("test").build();
        Thread.sleep(1L);
        Message copy = message.copy().build();

        assertThat(message.getTimestamp()).isNotEqualTo(copy.getTimestamp());
    }

    @Test
    void copy_isNotEqual() {
        Message message = Message.message("test").build();
        Message copy = message.copy().build();

        assertThat(message).isNotEqualTo(copy);
    }

    @Test
    void type_isConversation() {
        Channel channel = Channel.channel("test");
        Message message = Message.message("test").conversation(channel).build();
        assertThat(message.getType()).isEqualTo(Message.Type.CONVERSATION);

        Message test = Message.message("test").to(channel).build();
        assertThat(test.getType()).isEqualTo(Message.Type.CONVERSATION);
    }

    @Test
    void type_isConversation_withDirectConversation() {
        Chatter source = Chatter.player(server.addPlayer());
        Chatter target = Chatter.player(server.addPlayer());
        Message message = source.message("test").to(target).build();

        assertThat(message.getType()).isEqualTo(Message.Type.CONVERSATION);
    }

    @Test
    void type_isDefault_system() {
        Message message = Message.message("test").build();

        assertThat(message.getType()).isEqualTo(Message.Type.SYSTEM);
    }

    @Test
    void type_isSystem_withNoSource() {
        Message message = Message.message("test").build();

        assertThat(message.getType()).isEqualTo(Message.Type.SYSTEM);
    }

    @Test
    void send_oneSource_oneTarget_noChannel_createsDirectConversation() {
        Chatter source = ChatSource.player(server.addPlayer());
        Chatter target = ChatTarget.player(server.addPlayer());
        Message message = Message.message("Hi").from(source).to(target).send();

        assertThat(message.getConversation())
                .isNotNull()
                .isInstanceOf(PrivateConversation.class)
                .extracting(ChatTarget::getLastReceivedMessage)
                .isNotNull()
                .isEqualTo(message);
        assertThat(source.getActiveConversation()).isNotNull().isInstanceOf(PrivateConversation.class);
        assertThat(target.getActiveConversation()).isNotNull().isInstanceOf(PrivateConversation.class);
    }

    @Test
    void send_toChannelTarget_doesNotCreateDirectConversation() {
        ChatTarget channel = createChannel("test");
        Message message = Message.message("test").from(Chatter.player(server.addPlayer())).to(channel).send();
        assertThat(message.getConversation())
                .isNotInstanceOf(PrivateConversation.class)
                .isEqualTo(channel);
    }

    @Nested
    class Delete {

        private PlayerMock player;
        private Chatter chatter;
        private Message message;

        @BeforeEach
        void setUp() {
            player = server.addPlayer();
            chatter = Chatter.player(player);
            message = chatter.sendMessage("hi");
        }

        @Test
        void delete_removesMessageFromTargets() {
            assertThat(chatter.getReceivedMessages()).contains(message);
            message.delete();
            assertThat(chatter.getReceivedMessages()).doesNotContain(message);
        }

        @Test
        void delete_removesMessageFromUnreadMessages() {
            final Channel channel = createChannel("test");
            chatter.subscribe(channel);
            final Message message = Message.message("test").to(channel).send();
            assertThat(chatter.getUnreadMessages(channel)).contains(message);

            message.delete();
            assertThat(chatter.getUnreadMessages(channel)).doesNotContain(message);
        }

        @Test
        void delete_removesMessageEverywhere() {
            final Channel channel = createChannel("test", config -> config.sendToConsole(true).scope(Scopes.global()));
            chatter.setActiveConversation(channel);

            final Message message = channel.sendMessage("hey");
            message.delete();

            assertThat(channel.getReceivedMessages()).doesNotContain(message);
            assertThat(chatter.getReceivedMessages()).doesNotContain(message);
            assertThat(Console.console().getReceivedMessages()).doesNotContain(message);
            assertThat(plugin.getBungeecord().getReceivedMessages()).doesNotContain(message);
        }

        @Test
        void delete_updatesView() {
            final Channel channel = createChannel("test", config -> config.sendToConsole(true).scope(Scopes.global()));
            chatter.setActiveConversation(channel);
            final PlayerMock player2 = server.addPlayer();
            final Chatter chatter2 = Chatter.player(player2);
            chatter2.setActiveConversation(channel);
            final Message message = channel.sendMessage("foobar");

            message.delete();
            assertThat(cleaned(getLastMessage(player))).doesNotContain("foobar");
            assertThat(cleaned(getLastMessage(player2))).doesNotContain("foobar");
        }
    }
}