package net.silthus.schat.events.message;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelSettings;
import net.silthus.schat.events.Cancellable;
import net.silthus.schat.events.SChatEvent;
import net.silthus.schat.message.Message;

/**
 * The event is fired when a channel has the {@link ChannelSettings#GLOBAL} flag and
 * the message is about to be forwarded to another server.
 *
 * @since next
 */
@Getter
@Setter
@Accessors(fluent = true)
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"channel", "message"})
public final class SendGlobalMessageEvent implements SChatEvent, Cancellable {
    private final Channel channel;
    private final Message message;
    private final AtomicBoolean cancellationState = new AtomicBoolean(false);
}
