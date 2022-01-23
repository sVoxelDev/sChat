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

package net.silthus.schat.message;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.message.MessageHelper.randomText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class MessageTest implements Messenger {

    private boolean processCalled = false;
    private boolean deliverCalled = false;

    @Test
    void has_unique_id() {
        final Message message = message();
        assertThat(message.id()).isNotNull();
        assertThat(message.id()).isNotEqualTo(message().id());
    }

    @Test
    void given_same_id_are_equal() {
        final UUID id = UUID.randomUUID();
        final Message message = message().id(id).send(this);
        final Message message2 = message().id(id).send(this);
        assertThat(message).isEqualTo(message2);
    }

    @Nested class given_empty_message {
        private Message.Draft message;

        @BeforeEach
        void setUp() {
            message = message();
        }

        @Test
        void then_source_is_nil_source() {
            assertThat(message.source()).isEqualTo(Identity.nil());
        }

        @Test
        void then_has_timestamp() {
            assertThat(message.timestamp()).isCloseTo(now(), within(100L, MILLIS));
        }
    }

    @Test
    void given_null_text_uses_empty_component() {
        assertThat(message().text(null).text()).isEqualTo(Component.empty());
    }

    @Test
    void given_no_text_uses_empty_component() {
        assertThat(message().text()).isEqualTo(Component.empty());
    }

    @Test
    void given_text_sets_text() {
        Component text = randomText();
        assertThat(message().text(text).text()).isEqualTo(text);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void send_given_null_messenger_throws_npe() {
        assertNPE(() -> message().send(null));
    }

    @Test
    void send_calls_process_on_messenger() {
        message().send(this);
        assertThat(processCalled).isTrue();
    }

    @Test
    void send_creates_message() {
        final Message message = message().send(this);
        assertThat(message).isNotNull();
    }

    @Test
    void given_process_returns_different_message_then_send_uses_returned_processed_message() {
        final Message message = message().send(new ProcessedMessageMessengerStub());
        assertThat(message.id()).isEqualTo(ProcessedMessageMessengerStub.STUB_ID);
    }

    @Test
    void send_calls_deliver_on_messenger() {
        message().send(this);
        assertThat(deliverCalled).isTrue();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void to_given_null_throws_npe() {
        assertNPE(() -> message().to((MessageTarget) null));
    }

    @Test
    void given_no_targets_targets_are_empty() {
        assertThat(message().targets()).isEmpty();
    }

    @Test
    void to_given_chatter_adds_target() {
        final Chatter chatter = randomChatter();
        final Message.Draft draft = message().to(chatter);
        assertThat(draft.targets()).contains(chatter);
    }

    @Test
    void to_same_chatter_twice_adds_target_once() {
        Chatter chatter = randomChatter();
        Message.Draft draft = message().to(chatter).to(chatter);
        assertThat(draft.targets()).containsOnlyOnce(chatter);
    }

    @Test
    void given_channel_target_adds_channels_targets_to_message() {
        Channel channel = randomChannel();
        MessageTarget target = message -> {};
        channel.addTarget(target);
        message().to(channel);
    }

    @Test
    void given_channel_stores_channel_in_message() {
        Channel channel = randomChannel();
        Message.Draft message = message().to(channel);
        assertThat(message.channels()).contains(channel);
    }

    @Override
    public Message.Draft process(Message.Draft message) {
        processCalled = true;
        return message;
    }

    @Override
    public void deliver(Message message) {
        deliverCalled = true;
    }
}
