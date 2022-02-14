/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.message;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.commands.SendMessageResult;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MessageTest {

    @Test
    void has_unique_id() {
        final Message.Draft message = message();
        assertThat(message.id()).isNotNull();
        assertThat(message.id()).isNotEqualTo(message().id());
    }

    @Test
    void given_same_id_are_equal() {
        final UUID id = UUID.randomUUID();
        final Message message = message().id(id).send();
        final Message message2 = message().id(id).send();
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

    @Nested class send {

        @Test
        void creates_message() {
            final Message message = message().send();
            assertThat(message)
                .isNotNull()
                .isInstanceOf(MessageImpl.class);
        }

        @Test
        void delivers_message_to_all_targets() {
            MessageTarget target = mock(MessageTarget.class);
            Message message = message().to(target).send();
            verify(target).sendMessage(message);
        }
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
    void given_channel_target_then_adds_channels_targets_to_message() {
        Channel channel = randomChannel();
        MessageTarget target = SendMessageResult::success;
        channel.addTarget(target);
        message().to(channel);
    }

    @Test
    void given_channel_then_stores_channel_in_message() {
        Channel channel = randomChannel();
        Message.Draft message = message().to(channel);
        assertThat(message.channels()).contains(channel);
    }
}
