package net.silthus.schat.events.channel;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.events.SChatEvent;

/**
 * The event is fired after a chatter has joined a channel.
 *
 * <p>The event will not fire on subsequent joins to a channel the chatter is already a member of.</p>
 */
public record PostChatterJoinChannelEvent(Chatter chatter, Channel channel) implements SChatEvent {
}
