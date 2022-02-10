package net.silthus.schat.commands;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.messenger.Messenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.commands.CreatePrivateChannelCommand.createPrivateChannel;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CreatePrivateChannelCommandTest {

    private final Messenger messenger = mock(Messenger.class);

    @BeforeEach
    void setUp() {
        CreatePrivateChannelCommand.prototype(builder -> builder.messenger(messenger));
    }

    @Test
    void message_is_dispatched_to_messenger() {
        createPrivateChannel(randomChatter(), randomChatter());
        verify(messenger).sendPluginMessage(any());
    }

    @Test
    void update_private_channel_message_joins_targets() {
        final Channel channel = randomChannel();
        final ChatterMock target = randomChatter();
        channel.addTarget(target);
        new CreatePrivateChannelCommand.UpdatePrivateChannel(channel).process();
        target.assertJoinedChannel(channel);
    }
}
