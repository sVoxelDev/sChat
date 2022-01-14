package net.silthus.schat.policies;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;

public interface CanJoinChannel {
    boolean canJoinChannel(Chatter chatter, Channel channel);
}
