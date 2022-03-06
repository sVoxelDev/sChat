/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.silthus.schat.commands;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.channel.PrivateChannel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.Command;
import net.silthus.schat.command.CommandBuilder;
import net.silthus.schat.messenger.Messenger;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;

/**
 * Creates a new private channel between the two chatters.
 *
 * @since 1.0.0
 */
@Getter
@Accessors(fluent = true)
public class CreatePrivateChannelCommand implements Command {

    private static final @NonNull Predicate<Channel> IS_PRIVATE = channel -> channel.is(PRIVATE);
    @Getter(AccessLevel.PROTECTED)
    private static @NonNull Consumer<CreatePrivateChannelCommand.Builder> prototype = builder -> builder.channelSettings(PrivateChannel.prototype());

    public static void prototype(@NonNull Consumer<Builder> consumer) {
        prototype = prototype.andThen(consumer);
    }

    public static Result createPrivateChannel(Chatter source, Chatter target) {
        return createPrivateChannelBuilder(source, target).create().execute();
    }

    public static Builder createPrivateChannelBuilder(Chatter source, Chatter target) {
        final Builder builder = new Builder(source, target);
        prototype().accept(builder);
        return builder;
    }

    private final @NonNull Chatter source;
    private final @NonNull Chatter target;
    private final @NonNull ChannelRepository repository;
    private final @NonNull Messenger messenger;
    private final @NonNull Consumer<Channel.Builder> channelSettings;

    protected CreatePrivateChannelCommand(Builder builder) {
        this.source = builder.source;
        this.target = builder.target;
        this.repository = builder.channelRepository;
        this.messenger = builder.messenger;
        this.channelSettings = builder.channelSettings;
    }

    @Override
    public Result execute() throws Error {
        final Channel channel = repository.find(privateChannelFilter())
            .orElseGet(() -> createPrivateChannel(privateChannelKey(),
                source.displayName()
                .append(text("<->"))
                .append(target.displayName()))
            );

        updateChannelTargets(channel);

        return new Result(channel);
    }

    private void updateChannelTargets(Channel channel) {
        source.join(channel);
        target.join(channel);
    }

    @NotNull
    private Predicate<Channel> privateChannelFilter() {
        return IS_PRIVATE.and(channel -> channel.key().equals(privateChannelKey()));
    }

    private String privateChannelKey() {
        return String.valueOf(Set.of(source, target).hashCode());
    }

    private Channel createPrivateChannel(String key, Component name) {
        final Channel.Builder builder = Channel.channel(key).name(name);
        channelSettings().accept(builder);

        final Channel channel = builder.create();
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
        private Consumer<Channel.Builder> channelSettings = builder -> {};
        private ChannelRepository channelRepository;
        private Messenger messenger;

        protected Builder(Chatter source, Chatter target) {
            super(CreatePrivateChannelCommand::new);
            this.source = source;
            this.target = target;
        }
    }
}
