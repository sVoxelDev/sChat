package net.silthus.schat.commands;

import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.Command;
import net.silthus.schat.command.CommandBuilder;
import net.silthus.schat.command.Result;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.events.message.SendMessageEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Targets;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.Channel.GLOBAL;
import static net.silthus.schat.channel.Channel.PRIVATE;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;

@Getter
@Accessors(fluent = true)
public class SendMessageCommand implements Command {

    @Getter
    @Setter
    private static @NonNull Function<SendMessageCommand.Builder, SendMessageCommand.Builder> prototype = builder -> builder;

    public static SendMessageResult sendMessage(Message message) {
        return sendMessageBuilder(message).create().execute();
    }

    public static Builder sendMessageBuilder(Message message) {
        return prototype().apply(new Builder(message));
    }

    private final Message message;
    private final EventBus eventBus;
    private final ChannelRepository repository;

    protected SendMessageCommand(Builder builder) {
        this.message = builder.message;
        this.eventBus = builder.eventBus;
        this.repository = builder.channelRepository;
    }

    @Override
    public SendMessageResult execute() throws Error {
        return fireEventAndSendMessage(message);
    }

    private SendMessageResult fireEventAndSendMessage(Message message) {
        final SendMessageEvent event = eventBus.post(new SendMessageEvent(message));
        if (event.isNotCancelled())
            return sendMessage(event);
        return new SendMessageResult(message, false);
    }

    private SendMessageResult sendMessage(SendMessageEvent event) {
        if (targetsSingleChatter(event.targets()))
            return sendMessageToPrivateChannel(event);
        else
            return deliverMessage(event.targets(), event.message());
    }

    private SendMessageResult sendMessageToPrivateChannel(SendMessageEvent event) {
        if (event.message().source() instanceof Chatter source)
            return deliverMessage(createPrivateChannels(source, targetOf(event)), event.message());
        else
            return deliverMessage(event.targets(), event.message());
    }

    private SendMessageResult deliverMessage(MessageTarget target, Message message) {
        target.sendMessage(message);
        return new SendMessageResult(message, true);
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

    public record SendMessageResult(Message message, boolean success) implements Result {

        @Override
        public boolean wasSuccessful() {
            return success;
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class Builder extends CommandBuilder<Builder, SendMessageCommand> {

        private final Message message;
        private EventBus eventBus = EventBus.empty();
        private ChannelRepository channelRepository = createInMemoryChannelRepository();

        public Builder(Message message) {
            super(SendMessageCommand::new);
            this.message = message;
        }
    }
}
