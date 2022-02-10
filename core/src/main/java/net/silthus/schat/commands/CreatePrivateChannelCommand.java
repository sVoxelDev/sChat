package net.silthus.schat.commands;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.channel.ChannelSettings;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.Command;
import net.silthus.schat.command.CommandBuilder;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;

@Getter
@Accessors(fluent = true)
public class CreatePrivateChannelCommand implements Command {

    private static final @NonNull Predicate<Channel> IS_PRIVATE = channel -> channel.is(PRIVATE);

    @Getter
    @Setter
    private static @NonNull Function<CreatePrivateChannelCommand.Builder, CreatePrivateChannelCommand.Builder> prototype = builder -> builder;

    public static Result createPrivateChannel(Chatter source, Chatter target) {
        return createPrivateChannelBuilder(source, target).create().execute();
    }

    public static Builder createPrivateChannelBuilder(Chatter source, Chatter target) {
        return prototype.apply(new Builder(source, target));
    }

    private final Chatter source;
    private final Chatter target;
    private final ChannelRepository repository;

    protected CreatePrivateChannelCommand(Builder builder) {
        this.source = builder.source;
        this.target = builder.target;
        this.repository = builder.channelRepository;
    }

    @Override
    public Result execute() throws Error {
        final Channel channel = repository.find(privateChannel())
            .orElseGet(() -> createPrivateChannel(UUID.randomUUID().toString(), target.displayName()));

        source.join(channel);
        target.join(channel);

        return new Result(channel);
    }

    @NotNull
    private Predicate<Channel> privateChannel() {
        return IS_PRIVATE.and(containsTarget(source)).and(containsTarget(target));
    }

    @NotNull
    private Predicate<Channel> containsTarget(Chatter source) {
        return channel -> channel.targets().contains(source);
    }

    private Channel createPrivateChannel(String key, Component name) {
        final Channel channel = Channel.channel(key)
            .name(name)
            .set(ChannelSettings.GLOBAL, true)
            .set(PRIVATE, true)
            .create();
        repository.add(channel);
        return channel;
    }

    public record Result(Channel channel) implements net.silthus.schat.command.Result {
        @Override
        public boolean wasSuccessful() {
            return true;
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class Builder extends CommandBuilder<Builder, CreatePrivateChannelCommand> {

        private final Chatter source;
        private final Chatter target;
        private ChannelRepository channelRepository = createInMemoryChannelRepository();

        protected Builder(Chatter source, Chatter target) {
            super(CreatePrivateChannelCommand::new);
            this.source = source;
            this.target = target;
        }
    }
}
