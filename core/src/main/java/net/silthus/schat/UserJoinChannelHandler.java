package net.silthus.schat;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.handler.Handler;
import net.silthus.schat.handler.types.DefaultJoinChannelHandler;

public class UserJoinChannelHandler extends DefaultJoinChannelHandler implements Handler.JoinChannel {

    @Override
    public void joinChannel(Chatter chatter, Channel channel) {

        super.joinChannel(chatter, channel);
    }
}
