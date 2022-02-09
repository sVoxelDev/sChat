package net.silthus.schat.policies;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.ChatterMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.PRIVATE;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.policies.JoinPrivateChannelPolicy.canJoinPrivateChannel;
import static org.assertj.core.api.Assertions.assertThat;

class JoinPrivateChannelPolicyTest {
    private ChatterMock chatter;
    private Channel channel;

    @BeforeEach
    void setUp() {
        chatter = randomChatter();
        channel = channelWith(PRIVATE, true);
    }

    private void assertCanJoin(boolean expected) {
        assertThat(canJoinPrivateChannel(chatter, channel).validate()).isEqualTo(expected);
    }

    @Test
    void chatter_not_joined_then_cannot_join() {
        assertCanJoin(false);
    }

    @Test
    void given_channel_not_privaten_then_everbody_can_join() {
        channel = randomChannel();
        assertCanJoin(true);
    }

    @Test
    void joined_chatter_can_join() {
        channel.addTarget(chatter);
        assertCanJoin(true);
    }
}
