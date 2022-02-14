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