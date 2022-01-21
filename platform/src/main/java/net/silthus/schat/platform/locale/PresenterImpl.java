package net.silthus.schat.platform.locale;

import net.silthus.schat.usecases.JoinChannel;

import static net.silthus.schat.platform.locale.Messages.JOINED_CHANNEL;

final class PresenterImpl implements Presenter {

    static final PresenterImpl PRESENTER = new PresenterImpl();

    @Override
    public void joinedChannel(JoinChannel.Result result) {
        JOINED_CHANNEL.send(result.chatter(), result.channel());
    }
}
