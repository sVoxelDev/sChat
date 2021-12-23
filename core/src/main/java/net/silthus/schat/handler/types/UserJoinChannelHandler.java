package net.silthus.schat.handler.types;

import net.silthus.schat.User;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;

public final class UserJoinChannelHandler extends DefaultJoinChannelHandler {

    private final User user;

    public UserJoinChannelHandler(User user) {
        this.user = user;
    }

    @Override
    public void joinChannel(Chatter chatter, Channel channel) {
        if (!user.hasPermission("schat.channel." + channel.getKey() + ".join"))
            throw new Channel.AccessDenied();
        super.joinChannel(chatter, channel);
    }
}
