package net.silthus.schat.policies;

import java.util.function.BiPredicate;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;

import static net.silthus.schat.channel.ChannelSettings.PROTECTED;

public interface SendChannelMessagePolicy extends BiPredicate<Channel, Message>, Policy {

    SendChannelMessagePolicy ALLOW = (channel, message) -> true;
    SendChannelMessagePolicy DENY = (channel, message) -> false;

    SendChannelMessagePolicy SEND_CHANNEL_MESSAGE_POLICY = (channel, message) -> {
        if (message.source() instanceof Chatter chatter)
            return channel.isNot(PROTECTED) || chatter.isJoined(channel);
        else
            return true;
    };
}
