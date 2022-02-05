package net.silthus.schat.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.events.message.SendMessageEvent;
import org.jetbrains.annotations.NotNull;

final class MessengerImpl implements Messenger {

    static final SimpleMessenger SIMPLE = new SimpleMessenger();

    static MessengerImpl.Builder builder() {
        return new Builder();
    }

    private final EventBus eventBus;
    private final Messages messages = new Messages();

    private MessengerImpl(Builder builder) {
        this.eventBus = builder.eventBus;
    }

    @Override
    public @NotNull Message send(Message message) {
        if (messages.add(message))
            processMessage(message);
        return message;
    }

    private void processMessage(Message message) {
        final SendMessageEvent event = eventBus.post(new SendMessageEvent(message));
        if (event.isNotCancelled())
            event.targets().sendMessage(message);
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Builder implements Messenger.Builder {

        private EventBus eventBus = EventBus.empty();

        @Override
        public Messenger create() {
            return new MessengerImpl(this);
        }
    }

    private static final class SimpleMessenger implements Messenger {

        @Override
        public @NotNull Message send(Message message) {
            message.targets().sendMessage(message);
            return message;
        }
    }
}
