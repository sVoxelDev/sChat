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
package net.silthus.schat.policies;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.ChannelSettings.PROTECTED;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.policies.SendChannelMessagePolicy.SEND_CHANNEL_MESSAGE_POLICY;
import static org.assertj.core.api.Assertions.assertThat;

class SendChannelMessagePolicyTest {
    private Channel channel;
    private Message message;

    private void assertSuccess() {
        assertThat(SEND_CHANNEL_MESSAGE_POLICY.test(channel, message)).isTrue();
    }

    private void assertFailure() {
        assertThat(SEND_CHANNEL_MESSAGE_POLICY.test(channel, message)).isFalse();
    }

    @BeforeEach
    void setUp() {
        channel = randomChannel();
    }

    @Test
    void message_without_source_can_always_be_sent() {
        message = message().to(channel).create();
        assertSuccess();
    }

    @Test
    void message_with_chatter_source_to_unprotected_channel_can_be_sent() {
        channel = channelWith(PROTECTED, false);
        message = message().to(channel).source(randomChatter()).create();
        assertSuccess();
    }

    @Test
    void message_with_chatter_source_to_protected_channel_without_member_cannot_be_sent() {
        channel = channelWith(PROTECTED, true);
        message = message().source(randomChatter()).to(channel).create();
        assertFailure();
    }
}
