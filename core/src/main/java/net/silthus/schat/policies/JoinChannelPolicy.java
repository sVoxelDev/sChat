package net.silthus.schat.policies;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;

import static net.silthus.schat.policies.JoinPrivateChannelPolicy.canJoinPrivateChannel;
import static net.silthus.schat.policies.JoinProtectedChannelPolicy.canJoinProtectedChannel;

public interface JoinChannelPolicy {

    static Policy canJoinChannel(Chatter chatter, Channel channel) {
        return Policy.of(
            canJoinProtectedChannel(chatter, channel),
            canJoinPrivateChannel(chatter, channel)
        );
    }
}
