package net.silthus.chat;

import lombok.Value;

@Value
public class ChannelSubscription {

    Channel channel;
    ChatTarget target;
}
