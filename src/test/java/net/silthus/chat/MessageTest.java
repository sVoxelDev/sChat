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

import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.DirectConversation;
import net.silthus.chat.identities.Chatter;
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
        assertThat(toText(message)).isEqualTo("Hello");
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
        assertThat(toText(message)).isEqualTo("test");
    }

    @Test
    void message_withSource_usesDefaultFormat() {
        Message message = Message.message(ChatSource.named("test"), "Hi there!").build();

        assertThat(toText(message)).isEqualTo("test: Hi there!");
    }

    @Test
    void format_overrides_channelFormat() {
        Message message = ChatSource.named("test")
                .message("Hi")
                .to(Channel.channel("channel"))
                .format(Format.noFormat())
                .build();
        String text = toText(message);

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
        String text = toText(message);

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
    void type_isChannel() {
        Channel channel = Channel.channel("test");
        Message message = Message.message("test").conversation(channel).build();
        assertThat(message.getType()).isEqualTo(Message.Type.CHANNEL);

        Message test = Message.message("test").to(channel).build();
        assertThat(test.getType()).isEqualTo(Message.Type.CHANNEL);
    }

    @Test
    void type_isDefault_direct() {
        Message message = Message.message("test").build();

        assertThat(message.getType()).isEqualTo(Message.Type.SYSTEM);
    }

    @Test
    void type_isDrect_withSourceAndTarget() {
        Message message = Message.message("test")
                .from(ChatSource.player(server.addPlayer()))
                .to(ChatTarget.player(server.addPlayer()))
                .build();

        assertThat(message.getType()).isEqualTo(Message.Type.DIRECT);
    }

    @Test
    void type_isBroadcast_multipleTargets() {
        Message message = Message.message("test").to(ChatTarget.console(), ChatTarget.player(server.addPlayer())).build();

        assertThat(message.getType()).isEqualTo(Message.Type.BROADCAST);
    }

    @Test
    void send_oneSource_oneTarget_noChannel_createsDirectConversation() {
        Chatter source = ChatSource.player(server.addPlayer());
        Chatter target = ChatTarget.player(server.addPlayer());
        Message message = Message.message("Hi").from(source).to(target).send();

        assertThat(message.getConversation())
                .isNotNull()
                .isInstanceOf(DirectConversation.class)
                .extracting(ChatTarget::getLastReceivedMessage)
                .isNotNull()
                .isEqualTo(message);
        assertThat(source.getActiveConversation()).isNotNull().isInstanceOf(DirectConversation.class);
        assertThat(target.getActiveConversation()).isNotNull().isInstanceOf(DirectConversation.class);
    }

    @Test
    void send_toChannelTarget_doesNotCreateDirectConversation() {
        ChatTarget channel = createChannel("test");
        Message message = Message.message("test").from(Chatter.of(server.addPlayer())).to(channel).send();
        assertThat(message.getConversation())
                .isNotInstanceOf(DirectConversation.class)
                .isEqualTo(channel);
    }
}