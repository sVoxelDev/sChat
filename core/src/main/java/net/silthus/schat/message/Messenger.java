package net.silthus.schat.message;

import net.silthus.schat.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

public interface Messenger {
    /**
     * Gets a messenger that does not contain any processing logic and simply forwards the message to all targets.
     *
     * @return a simple messenger
     */
    static Messenger simpleMessenger() {
        return MessengerImpl.SIMPLE;
    }

    static Messenger.Builder messenger() {
        return MessengerImpl.builder();
    }

    @NotNull Message send(Message message);

    interface Builder {

        Builder eventBus(EventBus eventBus);

        Messenger create();
    }
}
