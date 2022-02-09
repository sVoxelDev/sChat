package net.silthus.schat.events.channel;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.events.Cancellable;
import net.silthus.schat.events.SChatEvent;
import net.silthus.schat.policies.Policy;

@Getter
@Setter
@Accessors(fluent = true)
public class PreJoinChannelEvent implements SChatEvent, Cancellable {
    private final Chatter chatter;
    private final Channel channel;
    private final AtomicBoolean cancellationState = new AtomicBoolean(false);
    private Policy policy;

    public PreJoinChannelEvent(Chatter chatter, Channel channel, Policy policy) {
        this.chatter = chatter;
        this.channel = channel;
        this.policy = policy;
    }
}
