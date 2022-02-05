package net.silthus.schat.message;

import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

public interface SendMessage {

    static SendMessage.Builder sendMessageUseCase() {
        return SendMessageUseCase.builder();
    }

    @NotNull Message send(Message message);

    interface Builder {

        Builder eventBus(EventBus eventBus);

        Builder channelRepository(ChannelRepository repository);

        SendMessage create();
    }
}
