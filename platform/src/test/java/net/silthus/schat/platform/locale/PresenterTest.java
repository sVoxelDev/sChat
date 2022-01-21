package net.silthus.schat.platform.locale;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.usecases.JoinChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.platform.locale.Messages.JOINED_CHANNEL;

class PresenterTest {

    private Presenter presenter;

    @BeforeEach
    void setUp() {
        presenter = Presenter.defaultPresenter();
    }

    @Test
    void given_join_channel_success_then_sends_message_to_chatter() {
        final ChatterMock chatter = randomChatter();
        final Channel channel = randomChannel();
        presenter.joinedChannel(new JoinChannel.Result(chatter, channel));
        chatter.assertReceivedMessage(JOINED_CHANNEL.build(channel));
    }
}
