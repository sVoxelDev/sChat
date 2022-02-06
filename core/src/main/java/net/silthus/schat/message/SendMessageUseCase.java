package net.silthus.schat.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.events.message.SendMessageEvent;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.Channel.GLOBAL;
import static net.silthus.schat.channel.Channel.PRIVATE;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;

final class SendMessageUseCase implements SendMessage {

    private final EventBus eventBus;
    private final ChannelRepository repository;
    private final Messages messages = new Messages();

    private SendMessageUseCase(Builder builder) {
        this.eventBus = builder.eventBus;
        this.repository = builder.channelRepository;
    }

    static SendMessageUseCase.Builder builder() {
        return new Builder();
    }

    @Override
    public @NotNull Message send(Message message) {
        if (messages.add(message))
            fireEventAndProcessMessage(message);
        return message;
    }

    private void fireEventAndProcessMessage(Message message) {
        final SendMessageEvent event = eventBus.post(new SendMessageEvent(message));
        if (event.isNotCancelled())
            processMessage(event);
    }

    private void processMessage(SendMessageEvent event) {
        if (targetsSingleChatter(event.targets()))
            sendMessageToPrivateChannel(event);
        else
            deliverMessage(event);
    }

    private void sendMessageToPrivateChannel(SendMessageEvent event) {
        if (event.message().source() instanceof Chatter source)
            createPrivateChannels(source, targetOf(event)).sendMessage(event.message());
        else
            deliverMessage(event);
    }

    private void deliverMessage(SendMessageEvent event) {
        event.targets().sendMessage(event.message());
    }

    private Chatter targetOf(SendMessageEvent event) {
        return (Chatter) event.targets().filter(MessageTarget.IS_CHATTER).get(0);
    }

    @NotNull
    private Channel createPrivateChannels(Chatter source, Chatter target) {
        final Channel sourceChannel = createPrivateChannel(source, target);
        final Channel targetChannel = createPrivateChannel(target, source);
        sourceChannel.addTarget(targetChannel);
        targetChannel.addTarget(sourceChannel);
        return sourceChannel;
    }

    @NotNull
    private Channel createPrivateChannel(Chatter source, Chatter target) {
        final String key = target.uniqueId().toString();
        final Channel channel = repository.findOrCreate(key, k -> createPrivateChannel(key, target.displayName()));
        source.join(channel);
        return channel;
    }

    private Channel createPrivateChannel(String key, Component name) {
        return Channel.channel(key)
            .name(name)
            .set(GLOBAL, true)
            .set(PRIVATE, true)
            .create();
    }

    private boolean targetsSingleChatter(Targets targets) {
        return targets.filter(MessageTarget.IS_CHATTER).size() == 1;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Builder implements SendMessage.Builder {

        private EventBus eventBus = EventBus.empty();
        private ChannelRepository channelRepository = createInMemoryChannelRepository();

        @Override
        public SendMessage create() {
            return new SendMessageUseCase(this);
        }
    }
}
