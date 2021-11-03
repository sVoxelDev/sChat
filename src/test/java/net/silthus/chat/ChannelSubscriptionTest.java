package net.silthus.chat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelSubscriptionTest extends TestBase {

    @Test
    void create() {
        Channel channel = ChatSource.channel("test");
        ChatTarget target = ChatTarget.player(server.addPlayer());

        ChannelSubscription subscription = new ChannelSubscription(channel, target);

        assertThat(subscription)
                .extracting(
                        ChannelSubscription::getChannel,
                        ChannelSubscription::getTarget
                ).contains(
                        channel,
                        target
                );
    }
}